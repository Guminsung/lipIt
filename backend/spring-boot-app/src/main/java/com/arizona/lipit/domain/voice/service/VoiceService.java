package com.arizona.lipit.domain.voice.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arizona.lipit.domain.member.entity.Member;
import com.arizona.lipit.domain.member.repository.MemberRepository;
import com.arizona.lipit.domain.voice.dto.CelebVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.SelectVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.SelectVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.UserVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.VoiceResponseDto;
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

@Service
@RequiredArgsConstructor
public class VoiceService {

	private final MemberRepository memberRepository;
	private final MemberVoiceRepository memberVoiceRepository;
	private final VoiceRepository voiceRepository;
	private final VoiceMapper voiceMapper;

	@Transactional(readOnly = true)
	public List<CelebVoiceResponseDto> getCelebVoicesByMemberId(Long memberId) {
		// memberId 유효성 검사
		if (memberId == null) {
			throw new CustomException(ErrorCode.INVALID_MEMBER_ID);
		}

		try {
			// 회원 확인
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));

			// 셀럽 음성 조회
			List<Voice> celebVoices = voiceRepository.findByType(VoiceType.CELEB);

			// 셀럽 음성이 없는 경우
			if (celebVoices.isEmpty()) {
				throw new CustomException(ErrorCode.CELEB_VOICE_NOT_FOUND);
			}

			// 회원의 레벨 정보 가져오기
			Level memberLevel = member.getLevel();
			int currentLevel = memberLevel != null ? memberLevel.getLevel() : 0;

			// 비즈니스 로직 및 맵핑 처리
			return celebVoices.stream()
				.map(voice -> {
					boolean activated = false;
					switch (voice.getVoiceName()) {
						case "스윙스":
							activated = currentLevel >= 1; // 레벨 1 이상
							break;
						case "카디비":
							activated = currentLevel >= 2; // 레벨 2 이상
							break;
						case "셀럽 C":
							activated = currentLevel >= 3; // 레벨 3 이상
							break;
						case "셀럽 D":
							activated = currentLevel >= 4; // 레벨 4 이상
							break;
						case "셀럽 E":
							activated = currentLevel >= 5; // 레벨 5
							break;
					}
					return voiceMapper.toCelebVoiceResponseDto(voice, activated);
				})
				.collect(Collectors.toList());
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional(readOnly = true)
	public List<VoiceResponseDto> getCustomVoicesByMemberId(Long memberId) {
		// memberId 유효성 검사
		if (memberId == null) {
			throw new CustomException(ErrorCode.INVALID_MEMBER_ID);
		}

		try {
			// 회원 확인
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));

			// 커스텀 음성 조회
			List<MemberVoice> memberVoices = memberVoiceRepository.findCustomVoicesByMemberId(memberId);

			// 커스텀 음성이 없는 경우
			if (memberVoices.isEmpty()) {
				throw new CustomException(ErrorCode.VOICE_NOT_FOUND);
			}

			return voiceMapper.toVoiceResponseDtoList(memberVoices);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(ErrorCode.VOICE_SERVER_ERROR);
		}
	}

	@Transactional(readOnly = true)
	public List<UserVoiceResponseDto> getAllVoicesByMemberId(Long memberId) {
		// memberId 유효성 검사
		if (memberId == null) {
			throw new CustomException(ErrorCode.INVALID_MEMBER_ID);
		}

		try {
			// 회원 확인
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));

			// 선택된 음성 ID가 없는 경우 빈 리스트 반환
			if (member.getSelectedVoiceId() == null) {
				return List.of();
			}

			// 선택된 음성을 voice 테이블에서 조회
			Voice selectedVoice = voiceRepository.findById(member.getSelectedVoiceId())
				.orElseThrow(() -> new CustomException(ErrorCode.VOICE_NOT_FOUND));

			return List.of(voiceMapper.toUserVoiceResponseDto(selectedVoice));
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(ErrorCode.VOICE_SERVER_ERROR);
		}
	}

	@Transactional
	public SelectVoiceResponseDto selectVoice(Long memberId, SelectVoiceRequestDto requestDto) {
		try {
			// voiceId 유효성 검사
			Long voiceId = requestDto.getVoiceId();
			if (voiceId == null || voiceId <= 0) {
				throw new CustomException(ErrorCode.BAD_REQUEST);
			}

			// 사용자 존재 여부 확인
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));

			// voiceId로 Voice 조회
			Voice selectedVoice = voiceRepository.findById(voiceId)
				.orElseThrow(() -> new CustomException(ErrorCode.VOICE_NOT_EXIST));

			// 커스텀 음성인 경우 본인 소유 확인
			if (selectedVoice.getType() == VoiceType.CUSTOM) {
				boolean isOwnVoice = memberVoiceRepository.findAllVoicesByMemberId(memberId).stream()
					.anyMatch(mv -> mv.getVoice().getVoiceId().equals(voiceId));

				if (!isOwnVoice) {
					throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
				}
			}

			// 선택한 음성 ID 업데이트
			member.setSelectedVoiceId(voiceId);
			memberRepository.save(member);

			// VoiceMapper를 사용하여 응답 생성
			return voiceMapper.toSelectVoiceResponseDto(member, selectedVoice);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(ErrorCode.VOICE_SERVER_ERROR);
		}
	}

	@Transactional
	public RecordingVoiceResponseDto saveRecordingVoice(RecordingVoiceRequestDto requestDto, Long memberId) {
		try {
			// 필수 필드 검증
			if (requestDto.getVoiceName() == null || requestDto.getAudioUrl() == null) {
				throw new CustomException(ErrorCode.INVALID_FORMAT, "필수 정보가 누락되었습니다.");
			}

			// URL 형식 검증
			try {
				new URL(requestDto.getAudioUrl());
				if (requestDto.getImageUrl() != null) {
					new URL(requestDto.getImageUrl());
				}
			} catch (MalformedURLException e) {
				throw new CustomException(ErrorCode.INVALID_URL);
			}

			// 사용자 존재 여부 확인
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));

			// 중복 음성 이름 확인
			List<MemberVoice> existingVoices = memberVoiceRepository.findAllVoicesByMemberId(memberId);
			boolean isDuplicate = existingVoices.stream()
				.anyMatch(mv -> mv.getVoice().getVoiceName().equals(requestDto.getVoiceName()));

			if (isDuplicate) {
				throw new CustomException(ErrorCode.VOICE_ALREADY_EXISTS);
			}

			// 새 Voice 엔티티 생성
			Voice voice = Voice.builder()
				.voiceName(requestDto.getVoiceName())
				.audioUrl(requestDto.getAudioUrl())
				.imageUrl(requestDto.getImageUrl())
				.type(VoiceType.CUSTOM)
				.build();

			Voice savedVoice = voiceRepository.save(voice);

			// MemberVoice 연결 엔티티 생성
			MemberVoice memberVoice = MemberVoice.builder()
				.member(member)
				.voice(savedVoice)
				.build();

			MemberVoice savedMemberVoice = memberVoiceRepository.save(memberVoice);

			// VoiceMapper를 사용하여 응답 생성
			return voiceMapper.toRecordingVoiceResponseDto(savedMemberVoice);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(ErrorCode.VOICE_SERVER_ERROR);
		}
	}
}