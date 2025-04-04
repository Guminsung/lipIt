package com.arizona.lipit.domain.voice.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arizona.lipit.domain.member.entity.Member;
import com.arizona.lipit.domain.member.repository.MemberRepository;
import com.arizona.lipit.domain.voice.dto.*;
import com.arizona.lipit.domain.voice.entity.MemberVoice;
import com.arizona.lipit.domain.voice.entity.Voice;
import com.arizona.lipit.domain.voice.entity.VoiceType;
import com.arizona.lipit.domain.voice.mapper.VoiceMapper;
import com.arizona.lipit.domain.voice.repository.MemberVoiceRepository;
import com.arizona.lipit.domain.voice.repository.VoiceRepository;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;
import com.arizona.lipit.domain.member.entity.Level;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceService {

	private final MemberRepository memberRepository;
	private final MemberVoiceRepository memberVoiceRepository;
	private final VoiceRepository voiceRepository;
	private final VoiceMapper voiceMapper;

	@Transactional(readOnly = true)
	public List<CelebVoiceResponseDto> getCelebVoicesByMemberId(Long memberId) {
		validateMemberId(memberId);
		Member member = findMemberById(memberId);
		List<Voice> celebVoices = findCelebVoices();
		return mapToCelebVoiceResponseDtos(celebVoices, member.getLevel());
	}

	@Transactional(readOnly = true)
	public List<VoiceResponseDto> getCustomVoicesByMemberId(Long memberId) {
		validateMemberId(memberId);
		findMemberById(memberId);
		return findAndMapCustomVoices(memberId);
	}

	@Transactional(readOnly = true)
	public List<UserVoiceResponseDto> getAllVoicesByMemberId(Long memberId) {
		validateMemberId(memberId);
		Member member = findMemberById(memberId);
		
		if (member.getSelectedVoiceId() == null) {
			return List.of();
		}

		Voice selectedVoice = findVoiceById(member.getSelectedVoiceId());
		return List.of(voiceMapper.toUserVoiceResponseDto(selectedVoice));
	}

	@Transactional
	public SelectVoiceResponseDto selectVoice(Long memberId, SelectVoiceRequestDto requestDto) {
		validateVoiceRequest(requestDto);
		Member member = findMemberById(memberId);
		Voice selectedVoice = findVoiceById(requestDto.getVoiceId());
		
		validateVoiceOwnership(memberId, selectedVoice);
		
		member.setSelectedVoiceId(selectedVoice.getVoiceId());
		memberRepository.save(member);
		
		return voiceMapper.toSelectVoiceResponseDto(member, selectedVoice);
	}

	@Transactional
	public RecordingVoiceResponseDto saveRecordingVoice(RecordingVoiceRequestDto requestDto, Long memberId) {
		validateRecordingRequest(requestDto, memberId);
		Member member = findMemberById(memberId);
		validateDuplicateVoiceName(memberId, requestDto.getVoiceName());

		Voice savedVoice = saveNewVoice(requestDto);
		MemberVoice savedMemberVoice = saveMemberVoice(member, savedVoice);
		
		return voiceMapper.toRecordingVoiceResponseDto(savedMemberVoice);
	}

	// Private helper methods
	private void validateMemberId(Long memberId) {
		if (memberId == null) {
			throw new CustomException(ErrorCode.INVALID_MEMBER_ID);
		}
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));
	}

	private List<Voice> findCelebVoices() {
		List<Voice> voices = voiceRepository.findByType(VoiceType.CELEB);
		if (voices.isEmpty()) {
			throw new CustomException(ErrorCode.CELEB_VOICE_NOT_FOUND);
		}
		return voices;
	}

	private List<VoiceResponseDto> findAndMapCustomVoices(Long memberId) {
		List<MemberVoice> memberVoices = memberVoiceRepository.findCustomVoicesByMemberId(memberId);
		if (memberVoices.isEmpty()) {
			throw new CustomException(ErrorCode.VOICE_NOT_FOUND);
		}
		return voiceMapper.toVoiceResponseDtoList(memberVoices);
	}

	private Voice findVoiceById(Long voiceId) {
		return voiceRepository.findById(voiceId)
			.orElseThrow(() -> new CustomException(ErrorCode.VOICE_NOT_EXIST));
	}

	private void validateVoiceRequest(SelectVoiceRequestDto requestDto) {
		if (requestDto.getVoiceId() == null || requestDto.getVoiceId() <= 0) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
	}

	private void validateVoiceOwnership(Long memberId, Voice voice) {
		if (voice.getType() == VoiceType.CUSTOM) {
			boolean isOwnVoice = memberVoiceRepository.findAllVoicesByMemberId(memberId).stream()
				.anyMatch(mv -> mv.getVoice().getVoiceId().equals(voice.getVoiceId()));
			
			if (!isOwnVoice) {
				throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
			}
		}
	}

	private void validateRecordingRequest(RecordingVoiceRequestDto requestDto, Long memberId) {
		if (requestDto == null || memberId == null) {
			throw new CustomException(ErrorCode.INVALID_FORMAT, "필수 정보가 누락되었습니다.");
		}

		if (isInvalidRequiredFields(requestDto)) {
			throw new CustomException(ErrorCode.INVALID_FORMAT, "필수 정보가 누락되었습니다.");
		}

		validateUrls(requestDto);
	}

	private boolean isInvalidRequiredFields(RecordingVoiceRequestDto requestDto) {
		return requestDto.getVoiceName() == null || requestDto.getVoiceName().trim().isEmpty() ||
			   requestDto.getAudioUrl() == null || requestDto.getAudioUrl().trim().isEmpty();
	}

	private void validateUrls(RecordingVoiceRequestDto requestDto) {
		try {
			new URL(requestDto.getAudioUrl());
			if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().trim().isEmpty()) {
				new URL(requestDto.getImageUrl());
			}
		} catch (MalformedURLException e) {
			throw new CustomException(ErrorCode.INVALID_URL, "올바르지 않은 URL 형식입니다.");
		}
	}

	private void validateDuplicateVoiceName(Long memberId, String voiceName) {
		boolean isDuplicate = memberVoiceRepository.findAllVoicesByMemberId(memberId).stream()
			.anyMatch(mv -> mv.getVoice().getVoiceName().trim().equals(voiceName.trim()));

		if (isDuplicate) {
			throw new CustomException(ErrorCode.VOICE_ALREADY_EXISTS, "이미 존재하는 음성 이름입니다.");
		}
	}

	private Voice saveNewVoice(RecordingVoiceRequestDto requestDto) {
		Voice voice = Voice.builder()
			.voiceName(requestDto.getVoiceName().trim())
			.audioUrl(requestDto.getAudioUrl().trim())
			.imageUrl(requestDto.getImageUrl() != null ? requestDto.getImageUrl().trim() : null)
			.type(VoiceType.CUSTOM)
			.build();
		
		return voiceRepository.save(voice);
	}

	private MemberVoice saveMemberVoice(Member member, Voice voice) {
		MemberVoice memberVoice = MemberVoice.builder()
			.member(member)
			.voice(voice)
			.build();
		
		return memberVoiceRepository.save(memberVoice);
	}

	private List<CelebVoiceResponseDto> mapToCelebVoiceResponseDtos(List<Voice> celebVoices, Level memberLevel) {
		int currentLevel = memberLevel != null ? memberLevel.getLevel() : 0;
		
		return celebVoices.stream()
			.map(voice -> {
				boolean activated = determineVoiceActivation(voice.getVoiceName(), currentLevel);
				return voiceMapper.toCelebVoiceResponseDto(voice, activated);
			})
			.collect(Collectors.toList());
	}

	private boolean determineVoiceActivation(String voiceName, int currentLevel) {
		return switch (voiceName) {
			case "스윙스" -> currentLevel >= 1;
			case "아리아나 그란데" -> currentLevel >= 2;
			case "베네딕트 컴버배치" -> currentLevel >= 3;
			case "마이클잭슨" -> currentLevel >= 4;
			case "MIJI" -> currentLevel >= 5;
			default -> false;
		};
	}
}