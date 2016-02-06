/*
 * Copyright (c) 2016, ProductLayer GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.ProductImage;
import com.productlayer.core.beans.UserAvatarImage;
import com.productlayer.core.beans.reports.ProblemReport;

import java.util.concurrent.Future;

public class ImageService {

    /**
     * Deletes a specific product image. Only the owner or an admin can delete the image. If the user earns
     * points for this operation 'X-ProductLayer-User-Points' and 'X-ProductLayer-User-Points-Changed' will be
     * present in the response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param imageID
     *         The identifier of the image
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         metadata of any deleted images
     * @return a Future object to optionally wait for the {@code ProductImage[]} result or to cancel the query
     */
    public static Future<ProductImage[]> deleteProductImage(final PLYAndroid client, final String imageID,
            PLYCompletion<ProductImage[]> completion) {
        return client.submit(new PLYAndroid.Query<ProductImage[]>() {
            @Override
            public ProductImage[] execute() {
                return com.productlayer.rest.client.services.ImageService.deleteProductImage(client
                        .getRestClient(), imageID);
            }
        }, completion);
    }

    /**
     * Deletes the avatar image of a user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         metadata of the deleted user avatar image
     * @return a Future object to optionally wait for the {@code UserAvatarImage} result or to cancel the
     * query
     */
    public static Future<UserAvatarImage> deleteUserAvatar(final PLYAndroid client, final String userID,
            PLYCompletion<UserAvatarImage> completion) {
        return client.submit(new PLYAndroid.Query<UserAvatarImage>() {
            @Override
            public UserAvatarImage execute() {
                return com.productlayer.rest.client.services.ImageService.deleteUserAvatar(client
                        .getRestClient(), userID);
            }
        }, completion);
    }

    /**
     * Downvotes a specific product image. If the user already up voted the image the up-vote will be
     * removed.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param imageID
     *         The identifier of the metadata object or the identifier of the file ({image_id}.jpg)
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The image
     *         metadata with the new vote score
     * @return a Future object to optionally wait for the {@code ProductImage} result or to cancel the query
     */
    public static Future<ProductImage> downVoteProductImage(final PLYAndroid client, final String imageID,
            PLYCompletion<ProductImage> completion) {
        return client.submit(new PLYAndroid.Query<ProductImage>() {
            @Override
            public ProductImage execute() {
                return com.productlayer.rest.client.services.ImageService.downVoteProductImage(client
                        .getRestClient(), imageID);
            }
        }, completion);
    }

    /**
     * Gets the default image (highest voted image) of a product. For some browsers you need to add the .jpg
     * file extension (/product/{gtin}/default_image.jpg) to the url.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param maxWidth
     *         [Optional] The preferred maximum width
     * @param maxHeight
     *         [Optional] The preferred maximum height
     * @param crop
     *         [Optional] Whether the image should be cropped
     * @param quality
     *         [Optional] The quality of the image between 20 and 100
     * @return the URL to the requested data
     */
    public static String getDefaultProductImageForSizeURL(final PLYAndroid client, final String gtin, final
    Integer maxWidth, final Integer maxHeight, final Boolean crop, final Integer quality) {
        return com.productlayer.rest.client.services.ImageService.getDefaultProductImageForSizeURL(client
                .getRestClient(), gtin, maxWidth, maxHeight, crop, quality);
    }

    /**
     * Gets the metadata of the default image (highest voted image) of a specific product.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The image
     *         metadata
     * @return a Future object to optionally wait for the {@code ProductImage} result or to cancel the query
     */
    public static Future<ProductImage> getDefaultProductImageMeta(final PLYAndroid client, final String
            gtin, PLYCompletion<ProductImage> completion) {
        return client.submit(new PLYAndroid.Query<ProductImage>() {
            @Override
            public ProductImage execute() {
                return com.productlayer.rest.client.services.ImageService.getDefaultProductImageMeta(client
                        .getRestClient(), gtin);
            }
        }, completion);
    }

    /**
     * Gets a specific image. For some browsers you need to add the .jpg file extension
     * (/image/{image_id}.jpg) to the url.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param imageID
     *         The identifier of the image
     * @param maxWidth
     *         [Optional] The preferred maximum width
     * @param maxHeight
     *         [Optional] The preferred maximum height
     * @param crop
     *         [Optional] Whether the image should be cropped
     * @param quality
     *         [Optional] The quality of the image between 20 and 100
     * @return the URL to the requested data
     */
    public static String getImageForSizeURL(final PLYAndroid client, final String imageID, final Integer
            maxWidth, final Integer maxHeight, final Boolean crop, final Integer quality) {
        return com.productlayer.rest.client.services.ImageService.getImageForSizeURL(client.getRestClient()
                , imageID, maxWidth, maxHeight, crop, quality);
    }

    /**
     * Gets a specific image meta information.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param imageID
     *         The identifier of the image
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The image
     *         metadata
     * @return a Future object to optionally wait for the {@code ProductImage} result or to cancel the query
     */
    public static Future<ProductImage> getImageMeta(final PLYAndroid client, final String imageID,
            PLYCompletion<ProductImage> completion) {
        return client.submit(new PLYAndroid.Query<ProductImage>() {
            @Override
            public ProductImage execute() {
                return com.productlayer.rest.client.services.ImageService.getImageMeta(client.getRestClient
                        (), imageID);
            }
        }, completion);
    }

    /**
     * Gets all image metadata of a specific product. Use this to get all image URLs of a product.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any found
     *         product image metadata
     * @return a Future object to optionally wait for the {@code ProductImage[]} result or to cancel the query
     */
    public static Future<ProductImage[]> getProductImages(final PLYAndroid client, final String gtin,
            PLYCompletion<ProductImage[]> completion) {
        return client.submit(new PLYAndroid.Query<ProductImage[]>() {
            @Override
            public ProductImage[] execute() {
                return com.productlayer.rest.client.services.ImageService.getProductImages(client
                        .getRestClient(), gtin);
            }
        }, completion);
    }

    /**
     * Gets the avatar of a specific user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param size
     *         [Optional] The size of the avatar image in pixel. The avatar image is always a square image and
     *         the maximum size is 512 pixel.
     * @return the URL to the requested data
     */
    public static String getUserAvatarURL(final PLYAndroid client, final String userID, final Integer size) {
        return com.productlayer.rest.client.services.ImageService.getUserAvatarURL(client.getRestClient(),
                userID, size);
    }

    /**
     * Sends a report about copyright infringements or any other problems with the image.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param imageID
     *         The identifier of the image
     * @param report
     *         The report
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         problem report object
     * @return a Future object to optionally wait for the {@code ProblemReport} result or to cancel the query
     */
    public static Future<ProblemReport> reportImage(final PLYAndroid client, final String imageID, final
    ProblemReport report, PLYCompletion<ProblemReport> completion) {
        return client.submit(new PLYAndroid.Query<ProblemReport>() {
            @Override
            public ProblemReport execute() {
                return com.productlayer.rest.client.services.ImageService.reportImage(client.getRestClient
                        (), imageID, report);
            }
        }, completion);
    }

    /**
     * Rotates the image clockwise.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param imageID
     *         The identifier of the image
     * @param degrees
     *         [Optional] The degrees to rotate the image clockwise
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The image
     *         metadata after the rotation
     * @return a Future object to optionally wait for the {@code ProductImage} result or to cancel the query
     */
    public static Future<ProductImage> rotateImage(final PLYAndroid client, final String imageID, final
    Integer degrees, PLYCompletion<ProductImage> completion) {
        return client.submit(new PLYAndroid.Query<ProductImage>() {
            @Override
            public ProductImage execute() {
                return com.productlayer.rest.client.services.ImageService.rotateImage(client.getRestClient
                        (), imageID, degrees);
            }
        }, completion);
    }

    /**
     * Upvotes a specific product image. If the user already down voted the image the down-vote will be
     * removed.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param imageID
     *         The identifier of the metadata object or the identifier of the file ({image_id}.jpg)
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The image
     *         metadata with the new vote score
     * @return a Future object to optionally wait for the {@code ProductImage} result or to cancel the query
     */
    public static Future<ProductImage> upVoteProductImage(final PLYAndroid client, final String imageID,
            PLYCompletion<ProductImage> completion) {
        return client.submit(new PLYAndroid.Query<ProductImage>() {
            @Override
            public ProductImage execute() {
                return com.productlayer.rest.client.services.ImageService.upVoteProductImage(client
                        .getRestClient(), imageID);
            }
        }, completion);
    }

    /**
     * Updates the avatar image of a user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param filePath
     *         the path to the file to upload
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         metadata of the updated user avatar image
     * @return a Future object to optionally wait for the {@code UserAvatarImage} result or to cancel the
     * query
     */
    public static Future<UserAvatarImage> updateUserAvatar(final PLYAndroid client, final String userID,
            final String filePath, PLYCompletion<UserAvatarImage> completion) {
        return client.submit(new PLYAndroid.Query<UserAvatarImage>() {
            @Override
            public UserAvatarImage execute() {
                return com.productlayer.rest.client.services.ImageService.updateUserAvatar(client
                        .getRestClient(), userID, filePath);
            }
        }, completion);
    }

    /**
     * Uploads an image for an opine. If the user earns points for this operation 'X-ProductLayer-User-Points'
     * and 'X-ProductLayer-User-Points-Changed' will be present in the response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param opineID
     *         The identifier of the opine
     * @param filePath
     *         the path to the file to upload
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         metadata of the uploaded image
     * @return a Future object to optionally wait for the {@code ProductImage} result or to cancel the query
     */
    public static Future<ProductImage> uploadOpineImage(final PLYAndroid client, final String opineID,
            final String filePath, PLYCompletion<ProductImage> completion) {
        return client.submit(new PLYAndroid.Query<ProductImage>() {
            @Override
            public ProductImage execute() {
                return com.productlayer.rest.client.services.ImageService.uploadOpineImage(client
                        .getRestClient(), opineID, filePath);
            }
        }, completion);
    }

    /**
     * Uploads a product image. If the user earns points for this operation 'X-ProductLayer-User-Points' and
     * 'X-ProductLayer-User-Points-Changed' will be present in the response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param filePath
     *         the path to the file to upload
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The image
     *         metadata
     * @return a Future object to optionally wait for the {@code ProductImage} result or to cancel the query
     */
    public static Future<ProductImage> uploadProductImage(final PLYAndroid client, final String gtin, final
    String filePath, PLYCompletion<ProductImage> completion) {
        return client.submit(new PLYAndroid.Query<ProductImage>() {
            @Override
            public ProductImage execute() {
                return com.productlayer.rest.client.services.ImageService.uploadProductImage(client
                        .getRestClient(), gtin, filePath);
            }
        }, completion);
    }
}
