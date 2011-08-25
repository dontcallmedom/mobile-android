
package me.openphoto.android.app.net;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * OpenPhotoApi provides access to the acOpenPhoto API.
 * 
 * @author Patrick Boos
 */
public class OpenPhotoApi extends ApiBase implements IOpenPhotoApi {

    private static IOpenPhotoApi sMock;

    /**
     * Constructor
     * 
     * @param baseUrl the base URL of the used OpenPhoto
     */
    protected OpenPhotoApi(String baseUrl) {
        super(baseUrl);
    }

    public static IOpenPhotoApi createInstance(String baseUrl) {
        if (sMock != null) {
            return sMock;
        } else {
            return new OpenPhotoApi(baseUrl);
        }
    }

    /*
     * (non-Javadoc)
     * @see me.openphoto.android.app.net.IOpenPhotoApi#getPhotos()
     */
    @Override
    public PhotosResponse getPhotos()
            throws ClientProtocolException, IllegalStateException, IOException, JSONException {
        return getPhotos(null, null, null);
    }

    /*
     * (non-Javadoc)
     * @see
     * me.openphoto.android.app.net.IOpenPhotoApi#getPhotos(me.openphoto.android
     * .app.net.ReturnSize)
     */
    @Override
    public PhotosResponse getPhotos(ReturnSize resize)
            throws ClientProtocolException, IllegalStateException, IOException, JSONException {
        return getPhotos(resize, null, null);
    }

    /*
     * (non-Javadoc)
     * @see
     * me.openphoto.android.app.net.IOpenPhotoApi#getPhotos(me.openphoto.android
     * .app.net.ReturnSize, int)
     */
    @Override
    public PhotosResponse getPhotos(ReturnSize resize, int page)
            throws ClientProtocolException, IllegalStateException, IOException, JSONException {
        return getPhotos(resize, null, new Paging(page));
    }

    /*
     * (non-Javadoc)
     * @see
     * me.openphoto.android.app.net.IOpenPhotoApi#getPhotos(me.openphoto.android
     * .app.net.ReturnSize, java.util.Collection)
     */
    @Override
    public PhotosResponse getPhotos(ReturnSize resize, Collection<String> tags)
            throws ClientProtocolException, IllegalStateException, IOException, JSONException {
        return getPhotos(resize, tags, null);
    }

    /*
     * (non-Javadoc)
     * @see
     * me.openphoto.android.app.net.IOpenPhotoApi#getPhotos(me.openphoto.android
     * .app.net.ReturnSize, java.util.Collection,
     * me.openphoto.android.app.net.Paging)
     */
    @Override
    public PhotosResponse getPhotos(ReturnSize resize, Collection<String> tags, Paging paging)
            throws ClientProtocolException, IOException, IllegalStateException, JSONException {
        ApiRequest request = new ApiRequest(ApiRequest.GET, "/photos.json");
        if (resize != null) {
            request.addParameter("returnSizes", resize.toString());
        }
        if (tags != null && !tags.isEmpty()) {
            Iterator<String> it = tags.iterator();
            StringBuilder sb = new StringBuilder(it.next());
            while (it.hasNext()) {
                sb.append("," + it.next());
            }
            request.addParameter("tags", sb.toString());
        }
        if (paging != null) {
            if (paging.hasPage()) {
                request.addParameter("page", Integer.toString(paging.getPage()));
            }
            if (paging.hasPageSize()) {
                request.addParameter("pageSize", Integer.toString(paging.getPageSize()));
            }
        }
        ApiResponse response = execute(request);
        return new PhotosResponse(new JSONObject(response.getContentAsString()));
    }

    /*
     * (non-Javadoc)
     * @see me.openphoto.android.app.net.IOpenPhotoApi#uploadPhoto(java.io.File,
     * me.openphoto.android.app.net.UploadMetaData)
     */
    @Override
    public UploadResponse uploadPhoto(File imageFile, UploadMetaData metaData)
            throws ClientProtocolException, IOException, IllegalStateException, JSONException {
        ApiRequest request = new ApiRequest(ApiRequest.POST, "/photo/upload.json");
        request.setMime(true);
        if (metaData.getTags() != null) {
            request.addParameter("tags", metaData.getTags());
        }
        if (metaData.getTitle() != null) {
            request.addParameter("title", metaData.getTitle());
        }
        if (metaData.getDescription() != null) {
            request.addParameter("description", metaData.getDescription());
        }
        if (metaData.getDescription() != null) {
            request.addParameter("description", metaData.getDescription());
        }
        request.addParameter("permission", Integer.toString(metaData.getPermission()));

        request.addFileParameter("photo", imageFile);
        ApiResponse response = execute(request);
        String responseString = response.getContentAsString();
        return new UploadResponse(new JSONObject(responseString));
    }

    /**
     * ONLY FOR TESTING! Will make the class return the given Mock when
     * accessing createInstance(..)
     * 
     * @param mock Mock to be used for OpenPhotoApi
     */
    private static void injectMock(IOpenPhotoApi mock) {
        OpenPhotoApi.sMock = mock;
    }
}
