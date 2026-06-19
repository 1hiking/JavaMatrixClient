package org.hik.payloads.instantmessaging;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

/**
 * This event represents a single audio clip.
 *
 * @param body          the filename of the original upload if {@code filename} is unset
 *                      or identical to it; otherwise, a caption for the audio.
 * @param file          information on the encrypted file, as specified in End-to-end
 *                      encryption. Required if the file is encrypted.
 * @param filename      the original filename of the uploaded file.
 * @param format        the format used in {@code formattedBody}. Required if
 *                      {@code formattedBody} is specified; currently only
 *                      {@code org.matrix.custom.html} is supported.
 * @param formattedBody the formatted version of {@code body}, when it acts as a caption.
 *                      Required if {@code format} is specified.
 * @param info          metadata for the audio clip referred to by {@code url}.
 * @param msgtype       always {@value #TYPE}.
 * @param url           required if the file is unencrypted.
 */
public record MatrixAudio(String body,
                          EncryptedFile file,
                          String filename,
                          String format,
                          @JsonProperty("formatted_body") String formattedBody,
                          AudioInfo info,
                          String msgtype,
                          URI url) implements MatrixEvent {

    public static final String TYPE = "m.audio";


    /**
     * Metadata describing an audio clip.
     *
     * @param duration the duration of the audio in milliseconds.
     */
    public record AudioInfo(Integer duration,
                     String mimetype,
                     Integer size
    ) implements HasInfo {
    }
}