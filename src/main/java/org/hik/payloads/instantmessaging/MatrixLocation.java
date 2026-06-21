package org.hik.payloads.instantmessaging;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * This message represents a real-world location.
 *
 * @param body   the filename of the original upload if {@code filename} is unset
 *               or identical to it; otherwise, a caption for the image.
 * @param info   metadata for the audio clip referred to by {@code url}.ç
 * @param geoUrI A geo URI (RFC5870) representing this location.
 */
public record MatrixLocation(String body,
                             @JsonProperty("geo_uri") String geoUrI,
                             LocationInfo info
) implements MatrixEvent {


    @Override
    public String msgtype() {
        return "m.location";
    }


    /**
     *
     * Additional information of the location data
     *
     * @param thumbnailFile information on the encrypted thumbnail file. Currently not supported.
     * @param thumbnailInfo metadata about the image referred to in {@code thumbnailUrl}.
     * @param thumbnailUrl  the URL to the thumbnail of the file. Only present if the thumbnail is unencrypted.
     */
    public record LocationInfo(EncryptedFile thumbnailFile,
                        ThumbnailInfo thumbnailInfo,
                        String thumbnailUrl
    ) implements HasThumbnail {
    }
}

