# app/graph/prompt_format/json_prompt_builder.py


def build_json_response_prompt(
    context: str = "", suffix: str = "", include_should_end: bool = True
) -> str:
    format_block = (
        "{\n"
        '  "en": "<Your English reply>",\n'
        '  "ko": "<Polite and natural Korean translation of the English reply>"'
    )
    if include_should_end:
        format_block += ',\n  "should_end_call": true or false'
    format_block += "\n}"

    system_prompt = (
        "You are an AI speaking on a phone call with a user.\n"
        "Respond in a friendly and natural tone in English.\n\n"
        "Your response must be returned in strict JSON format with no extra text or explanation.\n\n"
        f"{suffix}\n\n"
        "Format:\n"
        + format_block
        + "\n\nDo not include any markdown, explanations, or speaker labels like 'AI:'."
    )

    if context:
        system_prompt += "\n\nHere is relevant context:\n" + context

    return system_prompt
