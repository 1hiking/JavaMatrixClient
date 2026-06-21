package org.hik.payloads.instantmessaging;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

/**
 *
 * This message represents a single video clip.
 *
 * @param body          the filename of the original upload if {@code filename} is unset
 *                      or identical to it; otherwise, a caption for the video.
 * @param file          information on the encrypted file, as specified in End-to-end
 *                      encryption. Required if the file is encrypted.
 * @param filename      the original filename of the uploaded file.
 * @param format        the format used in {@code formattedBody}. Required if
 *                      {@code formattedBody} is specified; currently only
 *                      {@code org.matrix.custom.html} is supported.
 * @param formattedBody the formatted version of {@code body}, when it acts as a caption.
 *                      Required if {@code format} is specified.
 * @param info          metadata for the audio clip referred to by {@code url}.
 * @param url           required if the file is unencrypted.
 */
public record MatrixVideo(String body,
                          EncryptedFile file,
                          String filename,
                          String format,
                          @JsonProperty("formatted_body") String formattedBody,
                          VideoInfo info,
                          URI url
) implements MatrixEvent {


    @Override
    public String msgtype() {
        return "m.video";
    }


    /**
     *
     * Additional file information referred in the {@link MatrixAudio} {@code url} field.
     *
     * @param h             the height of the video in pixels.
     * @param w             The width of the video in pixels.
     * @param mimetype      the mimetype of the image.
     * @param size          the size of the image in bytes.
     * @param thumbnailFile information on the encrypted thumbnail file. Currently not supported.
     * @param thumbnailInfo metadata about the image referred to in {@code thumbnailUrl}.
     * @param thumbnailUrl  the URL to the thumbnail of the file. Only present if the thumbnail is unencrypted.
     * @param duration      the duration of the video in milliseconds.
     */
    public record VideoInfo(Integer duration,
                            Integer h,
                            String mimetype,
                            Integer size,
                            @JsonProperty("thumbnail_file") EncryptedFile thumbnailFile,
                            @JsonProperty("thumbnail_info") ThumbnailInfo thumbnailInfo,
                            @JsonProperty("thumbnail_url") String thumbnailUrl,
                            Integer w
    ) implements HasThumbnail, HasInfo {
    }
}
