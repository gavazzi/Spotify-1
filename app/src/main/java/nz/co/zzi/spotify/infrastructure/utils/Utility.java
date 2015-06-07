package nz.co.zzi.spotify.infrastructure.utils;

import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by joao.gavazzi on 7/06/15.
 */
public class Utility {

    @SuppressWarnings("unchecked")
    public static <T> T requireInstanceOf(Object instance, Class<T> what) {
        if(!what.isInstance(instance)) {
            throw new ClassCastException("The object must implement " + what.getName());
        }

        return (T) instance;
    }

    public static Image extractIdealSizeThumbnail(final int idealSizeInPx, final List<Image> images) {
        Image currentImage = null;
        for(final Image img : images) {
            if(currentImage == null) {
                currentImage = img;
            }

            //Try to find the best pick for the image, to avoid
            //distortion in case of pick a too small image, or
            //waste data in case of too big one
            if(img.width >= idealSizeInPx && img.width <= currentImage.width) {
                currentImage = img;
            }
        }

        return currentImage;
    }

    @SuppressWarnings("unchecked")
    public static <T> T [] convertParcelableArrayToGivenInstance(Parcelable [] parcelables, Class<T[]> clazz) {
        //It's necessary after the system be killed for
        //memory pressure (simulated by "Don't keep activities"
        //option), the OS returns Parcelable[] type instead of
        //the correct array type. This just ensure we are safe
        //in that situation
        T [] array;
        if(!clazz.isInstance(parcelables)) {
            array = Arrays.copyOf(parcelables, parcelables.length, clazz);
        } else {
            array = (T[]) parcelables;
        }

        return array;
    }
}
