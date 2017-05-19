package com.andlib.lp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;


public class BmpUtil {
	
	private final int BMP_WIDTH_OF_TIMES = 4;
	private final int BYTE_PER_PIXEL = 3;

	    private static final String TAG = BmpUtil.class.getSimpleName();

	public boolean save(Bitmap orgBitmap, String filePath){
		
		if(orgBitmap == null){
			return false;
		}

		if(filePath == null){
			return false;
		}

		boolean isSaveSuccess = true;


		int width = orgBitmap.getWidth();
		int height = orgBitmap.getHeight();

		//image dummy data size
		//reason : bmp file's width equals 4's multiple
		int dummySize = 0;
		byte[] dummyBytesPerRow = null;
		boolean hasDummy = false;
		if(isBmpWidth4Times(width)){
			hasDummy = true;
			dummySize = BMP_WIDTH_OF_TIMES - (width % BMP_WIDTH_OF_TIMES);
			dummyBytesPerRow = new byte[dummySize * BYTE_PER_PIXEL];
			for(int i = 0; i < dummyBytesPerRow.length; i++){
				dummyBytesPerRow[i] = (byte)0xFF;
			}
		}
 
		int[] pixels = new int[width * height];
		int imageSize = pixels.length * BYTE_PER_PIXEL + (height * dummySize * BYTE_PER_PIXEL);
		int imageDataOffset = 0x36;
		int fileSize = imageSize + imageDataOffset;

		//Android Bitmap Image Data
		orgBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		//ByteArrayOutputStream baos = new ByteArrayOutputStream(fileSize);
		ByteBuffer buffer = ByteBuffer.allocate(fileSize);

		try {

			buffer.put((byte)0x42);
			buffer.put((byte)0x4D);

			//size
			buffer.put(writeInt(fileSize));

			//reserved
			buffer.put(writeShort((short)0));
			buffer.put(writeShort((short)0));
		
			//image data start offset
			buffer.put(writeInt(imageDataOffset));
		

			//*******************************************
			//size
			buffer.put(writeInt(0x28));

			//width, height
			buffer.put(writeInt(width));
			buffer.put(writeInt(height));
		
			//planes
			buffer.put(writeShort((short)1));
		
			//bit count
			buffer.put(writeShort((short)24));
		
			//bit compression
			buffer.put(writeInt(0));
		
			//image data size
			buffer.put(writeInt(imageSize));
		
			//horizontal resolution in pixels per meter
			buffer.put(writeInt(0));
		
			//vertical resolution in pixels per meter (unreliable)
			buffer.put(writeInt(0));
		
			
			buffer.put(writeInt(0));
		
			
			buffer.put(writeInt(0));

			int row = height;
			int col = width;
			int startPosition = 0;
			int endPosition = 0;

			while( row > 0 ){
 	
				startPosition = (row - 1) * col;
				endPosition = row * col;
 		
				for(int i = startPosition; i < endPosition; i++ ){
					buffer.put(write24BitForPixcel(pixels[i]));
  	
					if(hasDummy){
						if(isBitmapWidthLastPixcel(width, i)){
							buffer.put(dummyBytesPerRow);
						}  			
					}
				}
				row--;
			}
 
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(buffer.array());
			fos.close();
	
		} catch (IOException e1) {
			e1.printStackTrace();
			isSaveSuccess = false;
		}
		finally{
	
		}

		return isSaveSuccess;
	}


	private boolean isBitmapWidthLastPixcel(int width, int i) {
		return i > 0 && (i % (width - 1)) == 0;
	}


	private boolean isBmpWidth4Times(int width) {
		return width % BMP_WIDTH_OF_TIMES > 0;
	}
	

	private byte[] writeInt(int value) throws IOException {
		byte[] b = new byte[4];
 	
		b[0] = (byte)(value & 0x000000FF);
		b[1] = (byte)((value & 0x0000FF00) >> 8);
		b[2] = (byte)((value & 0x00FF0000) >> 16);
		b[3] = (byte)((value & 0xFF000000) >> 24);
  
		return b;
	}
 

	private byte[] write24BitForPixcel(int value) throws IOException {
		byte[] b = new byte[3];
 	
		b[0] = (byte)(value & 0x000000FF);
		b[1] = (byte)((value & 0x0000FF00) >> 8);
		b[2] = (byte)((value & 0x00FF0000) >> 16);
  
		return b;
	}


	private byte[] writeShort(short value) throws IOException {
		byte[] b = new byte[2];
 	
		b[0] = (byte)(value & 0x00FF);
		b[1] = (byte)((value & 0xFF00) >> 8);
		
		return b;
	}
	
	


	   


	    public static BitmapFactory.Options calculateInSampleSize(
	            final BitmapFactory.Options options, final int reqWidth,
	            final int reqHeight) {

	        final int height = options.outHeight;
	        final int width = options.outWidth;
	        int inSampleSize = 1;
	        if (height > 400 || width > 450) {
	            if (height > reqHeight || width > reqWidth) {

	                final int heightRatio = Math.round((float) height
	                        / (float) reqHeight);
	                final int widthRatio = Math.round((float) width
	                        / (float) reqWidth);

	                inSampleSize = heightRatio < widthRatio ? heightRatio
	                        : widthRatio;
	            }
	        }

	        options.inSampleSize = inSampleSize;
	        options.inJustDecodeBounds = false;
	        return options;
	    }


	    public static Bitmap getBitmapFromResource(Resources res, int resId,
	                                               int reqWidth, int reqHeight) {
	        // BitmapFactory.Options options = new BitmapFactory.Options();
	        // options.inJustDecodeBounds = true;
	        // BitmapFactory.decodeResource(res, resId, options);
	        // options = BitmapHelper.calculateInSampleSize(options, reqWidth,
	        // reqHeight);
	        // return BitmapFactory.decodeResource(res, resId, options);


	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inPreferredConfig = Bitmap.Config.RGB_565;
	        options.inPurgeable = true;
	        options.inInputShareable = true;
	        InputStream is = res.openRawResource(resId);
	        return getBitmapFromStream(is, null, reqWidth, reqHeight);
	    }


	    public static Bitmap getBitmapFromFile(String pathName, int reqWidth,
	                                           int reqHeight) {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(pathName, options);
	        options = calculateInSampleSize(options, reqWidth, reqHeight);
	        return BitmapFactory.decodeFile(pathName, options);
	    }


	    public static Bitmap getBitmapFromByteArray(byte[] data, int offset,
	                                                int length, int reqWidth, int reqHeight) {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeByteArray(data, offset, length, options);
	        options = calculateInSampleSize(options, reqWidth, reqHeight);
	        return BitmapFactory.decodeByteArray(data, offset, length, options);
	    }


	    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	        return baos.toByteArray();
	    }


	    public static byte[] getBytesFromStream(InputStream inputStream) {
	        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
	        byte[] buffer = new byte[1024];
	        int len;
	        try {
	            while ((len = inputStream.read(buffer)) >= 0) {
	                os.write(buffer, 0, len);
	            }
	        } catch (java.io.IOException e) {
	            e.printStackTrace();
	        }
	        return os.toByteArray();
	    }


	    public static Bitmap getBitmapFromBytes(byte[] b) {
	        if (b.length != 0) {
	            return BitmapFactory.decodeByteArray(b, 0, b.length);
	        } else {
	            return null;
	        }
	    }


	    public static Bitmap getBitmapFromStream(InputStream is, int reqWidth,
	                                             int reqHeight) {
	        byte[] data = FileUtil.input2byte(is);
	        return getBitmapFromByteArray(data, 0, data.length, reqWidth, reqHeight);
	    }


	    public static Bitmap getBitmapFromStream(InputStream is, Rect outPadding,
	                                             int reqWidth, int reqHeight) {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(is, outPadding, options);
	        options = calculateInSampleSize(options, reqWidth, reqHeight);
	        return BitmapFactory.decodeStream(is, outPadding, options);
	    }


	    public static Bitmap getBitmapFromView(View view) {
	        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
	                Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(bitmap);

	        view.layout(view.getLeft(), view.getTop(), view.getRight(),
	                view.getBottom());
	        view.draw(canvas);

	        return bitmap;
	    }


	    public static Bitmap getBitmapFromView2(View view) {

	        view.clearFocus();
	        view.setPressed(false);
	        boolean willNotCache = view.willNotCacheDrawing();
	        view.setWillNotCacheDrawing(false);
	        int color = view.getDrawingCacheBackgroundColor();
	        view.setDrawingCacheBackgroundColor(0);
	        if (color != 0) {
	            view.destroyDrawingCache();
	        }
	        view.buildDrawingCache();
	        Bitmap cacheBitmap = view.getDrawingCache();
	        if (cacheBitmap == null) {
	                L.e(TAG, "failed getViewBitmap(" + view + ")"+ new RuntimeException().getStackTrace());
	             return null;
	        }
	        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
	        // Restore the view
	        view.destroyDrawingCache();
	        view.setWillNotCacheDrawing(willNotCache);
	        view.setDrawingCacheBackgroundColor(color);
	        return bitmap;
	    }


	    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
	        int width = drawable.getIntrinsicWidth();
	        int height = drawable.getIntrinsicHeight();
	        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
	                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
	                : Bitmap.Config.RGB_565);
	        Canvas canvas = new Canvas(bitmap);
	        drawable.setBounds(0, 0, width, height);
	        drawable.draw(canvas);
	        return bitmap;

	    }


	    public static Bitmap combineImages(Bitmap bgd, Bitmap fg) {
	        Bitmap bmp;

	        int width = bgd.getWidth() > fg.getWidth() ? bgd.getWidth() : fg
	                .getWidth();
	        int height = bgd.getHeight() > fg.getHeight() ? bgd.getHeight() : fg
	                .getHeight();

	        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	        Paint paint = new Paint();
	        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

	        Canvas canvas = new Canvas(bmp);
	        canvas.drawBitmap(bgd, 0, 0, null);
	        canvas.drawBitmap(fg, 0, 0, paint);

	        return bmp;
	    }


	    public static Bitmap combineImagesToSameSize(Bitmap bgd, Bitmap fg) {
	        Bitmap bmp;

	        int width = bgd.getWidth() < fg.getWidth() ? bgd.getWidth() : fg
	                .getWidth();
	        int height = bgd.getHeight() < fg.getHeight() ? bgd.getHeight() : fg
	                .getHeight();

	        if (fg.getWidth() != width && fg.getHeight() != height) {
	            fg = zoom(fg, width, height);
	        }
	        if (bgd.getWidth() != width && bgd.getHeight() != height) {
	            bgd = zoom(bgd, width, height);
	        }

	        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	        Paint paint = new Paint();
	        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

	        Canvas canvas = new Canvas(bmp);
	        canvas.drawBitmap(bgd, 0, 0, null);
	        canvas.drawBitmap(fg, 0, 0, paint);

	        return bmp;
	    }


	    public static Bitmap zoom(Bitmap bitmap, int w, int h) {
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        Matrix matrix = new Matrix();
	        float scaleWidht = ((float) w / width);
	        float scaleHeight = ((float) h / height);
	        matrix.postScale(scaleWidht, scaleHeight);
	        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
	                matrix, true);
	        return newbmp;
	    }


	    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	                bitmap.getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);

	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	        final RectF rectF = new RectF(rect);

	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmap, rect, rect, paint);

	        return output;
	    }


	    public static Bitmap createReflectionBitmap(Bitmap bitmap) {
	        final int reflectionGap = 4;
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();

	        Matrix matrix = new Matrix();
	        matrix.preScale(1, -1);

	        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
	                width, height / 2, matrix, false);

	        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
	                (height + height / 2), Config.ARGB_8888);

	        Canvas canvas = new Canvas(bitmapWithReflection);
	        canvas.drawBitmap(bitmap, 0, 0, null);
	        Paint deafalutPaint = new Paint();
	        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

	        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

	        Paint paint = new Paint();
	        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
	                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
	                0x00ffffff, TileMode.CLAMP);
	        paint.setShader(shader);
	        // Set the Transfer mode to be porter duff and destination in
	        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	        // Draw a rectangle using the paint with our linear gradient
	        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
	                + reflectionGap, paint);

	        return bitmapWithReflection;
	    }


	    public static Bitmap compressImage(Bitmap image) {

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	        int options = 100;
	        while (baos.toByteArray().length / 1024 > 100) {
	            baos.reset();
	            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
	            options -= 10;
	        }
	        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
	        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
	        return bitmap;
	    }


	    public static Bitmap convertGreyImg(Bitmap img) {
	        int width = img.getWidth();
	        int height = img.getHeight();

	        int[] pixels = new int[width * height];
	        img.getPixels(pixels, 0, width, 0, 0, width, height);
	        int alpha = 0xFF << 24;
	        for (int i = 0; i < height; i++) {
	            for (int j = 0; j < width; j++) {
	                int grey = pixels[width * i + j];

	                int red = ((grey & 0x00FF0000) >> 16);
	                int green = ((grey & 0x0000FF00) >> 8);
	                int blue = (grey & 0x000000FF);

	                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
	                grey = alpha | (grey << 16) | (grey << 8) | grey;
	                pixels[width * i + j] = grey;
	            }
	        }
	        Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
	        result.setPixels(pixels, 0, width, 0, 0, width, height);
	        return result;
	    }


	    public static Bitmap getRoundBitmap(Bitmap bitmap) {
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        float roundPx;
	        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
	        if (width <= height) {
	            roundPx = width / 2;
	            top = 0;
	            bottom = width;
	            left = 0;
	            right = width;
	            height = width;
	            dst_left = 0;
	            dst_top = 0;
	            dst_right = width;
	            dst_bottom = width;
	        } else {
	            roundPx = height / 2;
	            float clip = (width - height) / 2;
	            left = clip;
	            right = width - clip;
	            top = 0;
	            bottom = height;
	            width = height;
	            dst_left = 0;
	            dst_top = 0;
	            dst_right = height;
	            dst_bottom = height;
	        }

	        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);

	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect src = new Rect((int) left, (int) top, (int) right,
	                (int) bottom);
	        final Rect dst = new Rect((int) dst_left, (int) dst_top,
	                (int) dst_right, (int) dst_bottom);
	        final RectF rectF = new RectF(dst);

	        paint.setAntiAlias(true);

	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmap, src, dst, paint);
	        return output;
	    }


	    public static Bitmap createThumbnailBitmap(Bitmap bitmap, Context context) {
	        int sIconWidth = -1;
	        int sIconHeight = -1;
	        final Resources resources = context.getResources();
	        sIconWidth = sIconHeight = (int) resources
	                .getDimension(android.R.dimen.app_icon_size);

	        final Paint sPaint = new Paint();
	        final Rect sBounds = new Rect();
	        final Rect sOldBounds = new Rect();
	        Canvas sCanvas = new Canvas();

	        int width = sIconWidth;
	        int height = sIconHeight;

	        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
	                Paint.FILTER_BITMAP_FLAG));

	        final int bitmapWidth = bitmap.getWidth();
	        final int bitmapHeight = bitmap.getHeight();

	        if (width > 0 && height > 0) {
	            if (width < bitmapWidth || height < bitmapHeight) {
	                final float ratio = (float) bitmapWidth / bitmapHeight;

	                if (bitmapWidth > bitmapHeight) {
	                    height = (int) (width / ratio);
	                } else if (bitmapHeight > bitmapWidth) {
	                    width = (int) (height * ratio);
	                }

	                final Bitmap.Config c = (width == sIconWidth && height == sIconHeight) ? bitmap
	                        .getConfig() : Bitmap.Config.ARGB_8888;
	                final Bitmap thumb = Bitmap.createBitmap(sIconWidth,
	                        sIconHeight, c);
	                final Canvas canvas = sCanvas;
	                final Paint paint = sPaint;
	                canvas.setBitmap(thumb);
	                paint.setDither(false);
	                paint.setFilterBitmap(true);
	                sBounds.set((sIconWidth - width) / 2,
	                        (sIconHeight - height) / 2, width, height);
	                sOldBounds.set(0, 0, bitmapWidth, bitmapHeight);
	                canvas.drawBitmap(bitmap, sOldBounds, sBounds, paint);
	                return thumb;
	            } else if (bitmapWidth < width || bitmapHeight < height) {
	                final Bitmap.Config c = Bitmap.Config.ARGB_8888;
	                final Bitmap thumb = Bitmap.createBitmap(sIconWidth,
	                        sIconHeight, c);
	                final Canvas canvas = sCanvas;
	                final Paint paint = sPaint;
	                canvas.setBitmap(thumb);
	                paint.setDither(false);
	                paint.setFilterBitmap(true);
	                canvas.drawBitmap(bitmap, (sIconWidth - bitmapWidth) / 2,
	                        (sIconHeight - bitmapHeight) / 2, paint);
	                return thumb;
	            }
	        }

	        return bitmap;
	    }


	    public static Bitmap createWatermarkBitmap(Bitmap src, Bitmap watermark) {
	        if (src == null) {
	            return null;
	        }

	        int w = src.getWidth();
	        int h = src.getHeight();
	        int ww = watermark.getWidth();
	        int wh = watermark.getHeight();
	        // create the new blank bitmap
	        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
	        Canvas cv = new Canvas(newb);
	        // draw src into
	        cv.drawBitmap(src, 0, 0, null);
	        // draw watermark into
	        cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);
	        cv.save(Canvas.ALL_SAVE_FLAG);
	        cv.restore();
	        return newb;
	    }


	    public static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
	                               int quality) {
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        src.compress(format, quality, os);

	        byte[] array = os.toByteArray();
	        return BitmapFactory.decodeByteArray(array, 0, array.length);
	    }


	    public static void compress(Bitmap bitmap, double maxSize) {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
	        byte[] b = baos.toByteArray();
	        double mid = b.length / 1024;//b2kb
	        double i = mid / maxSize;
	        if (i > 1) {
	            bitmap = scale(bitmap, bitmap.getWidth() / Math.sqrt(i),
	                    bitmap.getHeight() / Math.sqrt(i));
	        }
	    }


	    public static Bitmap scale(Bitmap src, double newWidth, double newHeight) {

	        float width = src.getWidth();
	        float height = src.getHeight();
	        Matrix matrix = new Matrix();
	        float scaleWidth = ((float) newWidth) / width;
	        float scaleHeight = ((float) newHeight) / height;
	        matrix.postScale(scaleWidth, scaleHeight);
	        return Bitmap.createBitmap(src, 0, 0, (int) width, (int) height,matrix, true);
	    }


	    public static Bitmap scale(Bitmap src, Matrix scaleMatrix) {
	        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
	                scaleMatrix, true);
	    }


	    public static Bitmap scale(Bitmap src, float scaleX, float scaleY) {
	        Matrix matrix = new Matrix();
	        matrix.postScale(scaleX, scaleY);
	        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
	                matrix, true);
	    }


	    public static Bitmap scale(Bitmap src, float scale) {
	        return scale(src, scale, scale);
	    }


	    public static Bitmap rotate(Bitmap bitmap, int angle) {
	        Matrix matrix = new Matrix();
	        matrix.postRotate(angle);
	        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
	                bitmap.getHeight(), matrix, true);
	    }


	    public static Bitmap reverseByHorizontal(Bitmap bitmap) {
	        Matrix matrix = new Matrix();
	        matrix.preScale(-1, 1);
	        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
	                bitmap.getHeight(), matrix, false);
	    }


	    public static Bitmap reverseByVertical(Bitmap bitmap) {
	        Matrix matrix = new Matrix();
	        matrix.preScale(1, -1);
	        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
	                bitmap.getHeight(), matrix, false);
	    }


	    public static Bitmap adjustTone(Bitmap src, int delta) {
	        if (delta >= 24 || delta <= 0) {
	            return null;
	        }

	        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};//gauss matrix
	        int width = src.getWidth();
	        int height = src.getHeight();
	        Bitmap bitmap = Bitmap.createBitmap(width, height,
	                Bitmap.Config.RGB_565);

	        int pixR = 0;
	        int pixG = 0;
	        int pixB = 0;
	        int pixColor = 0;
	        int newR = 0;
	        int newG = 0;
	        int newB = 0;
	        int idx = 0;
	        int[] pixels = new int[width * height];

	        src.getPixels(pixels, 0, width, 0, 0, width, height);
	        for (int i = 1, length = height - 1; i < length; i++) {
	            for (int k = 1, len = width - 1; k < len; k++) {
	                idx = 0;
	                for (int m = -1; m <= 1; m++) {
	                    for (int n = -1; n <= 1; n++) {
	                        pixColor = pixels[(i + m) * width + k + n];
	                        pixR = Color.red(pixColor);
	                        pixG = Color.green(pixColor);
	                        pixB = Color.blue(pixColor);

	                        newR += (pixR * gauss[idx]);
	                        newG += (pixG * gauss[idx]);
	                        newB += (pixB * gauss[idx]);
	                        idx++;
	                    }
	                }
	                newR /= delta;
	                newG /= delta;
	                newB /= delta;
	                newR = Math.min(255, Math.max(0, newR));
	                newG = Math.min(255, Math.max(0, newG));
	                newB = Math.min(255, Math.max(0, newB));
	                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
	                newR = 0;
	                newG = 0;
	                newB = 0;
	            }
	        }
	        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	        return bitmap;
	    }


	    public static Bitmap convertToBlackWhite(Bitmap bmp) {
	        int width = bmp.getWidth();
	        int height = bmp.getHeight();
	        int[] pixels = new int[width * height];
	        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

	        int alpha = 0xFF << 24; //24bit
	        for (int i = 0; i < height; i++) {
	            for (int j = 0; j < width; j++) {
	                int grey = pixels[width * i + j];

	                int red = ((grey & 0x00FF0000) >> 16);
	                int green = ((grey & 0x0000FF00) >> 8);
	                int blue = (grey & 0x000000FF);

	                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
	                grey = alpha | (grey << 16) | (grey << 8) | grey;
	                pixels[width * i + j] = grey;
	            }
	        }
	        Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);
	        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
	        return newBmp;
	    }


	    public static int getImageDegree(String path) {
	        int degree = 0;
	        try {
	            ExifInterface exifInterface = new ExifInterface(path);
	            int orientation = exifInterface.getAttributeInt(
	                    ExifInterface.TAG_ORIENTATION,
	                    ExifInterface.ORIENTATION_NORMAL);
	            switch (orientation) {
	                case ExifInterface.ORIENTATION_ROTATE_90:
	                    degree = 90;
	                    break;
	                case ExifInterface.ORIENTATION_ROTATE_180:
	                    degree = 180;
	                    break;
	                case ExifInterface.ORIENTATION_ROTATE_270:
	                    degree = 270;
	                    break;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return degree;
	    }

	   

	    public static Bitmap saturation(Bitmap bitmap, int saturationValue) {
	        float newSaturationValue = saturationValue * 1.0F / 127;
	        ColorMatrix saturationColorMatrix = new ColorMatrix();
	        saturationColorMatrix.setSaturation(newSaturationValue);
	        Paint paint = new Paint();
	        paint.setColorFilter(new ColorMatrixColorFilter(saturationColorMatrix));
	        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
	                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(newBitmap);
	        canvas.drawBitmap(bitmap, 0, 0, paint);
	        return newBitmap;
	    }


	    public static Bitmap lum(Bitmap bitmap, int lumValue) {
	        float newlumValue = lumValue * 1.0F / 127;
	        ColorMatrix lumColorMatrix = new ColorMatrix();
	        lumColorMatrix.setScale(newlumValue, newlumValue, newlumValue, 1);
	        Paint paint = new Paint();
	        paint.setColorFilter(new ColorMatrixColorFilter(lumColorMatrix));
	        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
	                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(newBitmap);
	        canvas.drawBitmap(bitmap, 0, 0, paint);
	        return newBitmap;
	    }


	    public static Bitmap hue(Bitmap bitmap, int hueValue) {
	        float newHueValue = (hueValue - 127) * 1.0F / 127 * 180;
	        ColorMatrix hueColorMatrix = new ColorMatrix();
	        hueColorMatrix.setRotate(0, newHueValue);
	        hueColorMatrix.setRotate(1, newHueValue);
	        hueColorMatrix.setRotate(2, newHueValue);
	        Paint paint = new Paint();
	        paint.setColorFilter(new ColorMatrixColorFilter(hueColorMatrix));

	        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
	                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(newBitmap);

	        canvas.drawBitmap(bitmap, 0, 0, paint);
	        return newBitmap;
	    }


	    public static Bitmap lumAndHueAndSaturation(Bitmap bitmap, int lumValue,
	                                                int hueValue, int saturationValue) {

	        float newSaturationValue = saturationValue * 1.0F / 127;
	        float newlumValue = lumValue * 1.0F / 127;
	        float newHueValue = (hueValue - 127) * 1.0F / 127 * 180;
	        ColorMatrix colorMatrix = new ColorMatrix();
	        colorMatrix.setSaturation(newSaturationValue);
	        colorMatrix.setScale(newlumValue, newlumValue, newlumValue, 1);
	        colorMatrix.setRotate(0, newHueValue);
	        colorMatrix.setRotate(1, newHueValue);
	        colorMatrix.setRotate(2, newHueValue);
	        Paint paint = new Paint();
	        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
	        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
	                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(newBitmap);
	        canvas.drawBitmap(bitmap, 0, 0, paint);
	        return newBitmap;
	    }


	    public static Bitmap nostalgic(Bitmap bitmap) {
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        Bitmap newBitmap = Bitmap.createBitmap(width, height,
	                Bitmap.Config.RGB_565);
	        int pixColor = 0;
	        int pixR = 0;
	        int pixG = 0;
	        int pixB = 0;
	        int newR = 0;
	        int newG = 0;
	        int newB = 0;
	        int[] pixels = new int[width * height];
	        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	        for (int i = 0; i < height; i++) {
	            for (int k = 0; k < width; k++) {
	                pixColor = pixels[width * i + k];
	                pixR = Color.red(pixColor);
	                pixG = Color.green(pixColor);
	                pixB = Color.blue(pixColor);
	                newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
	                newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
	                newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
	                int newColor = Color.argb(255, newR > 255 ? 255 : newR,
	                        newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
	                pixels[width * i + k] = newColor;
	            }
	        }
	        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	        return newBitmap;
	    }


	    public static Bitmap soften(Bitmap bitmap) {

	        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};

	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        Bitmap newBitmap = Bitmap.createBitmap(width, height,
	                Bitmap.Config.RGB_565);

	        int pixR = 0;
	        int pixG = 0;
	        int pixB = 0;

	        int pixColor = 0;

	        int newR = 0;
	        int newG = 0;
	        int newB = 0;

	        int delta = 16; //bitmap-->ming,an

	        int idx = 0;
	        int[] pixels = new int[width * height];
	        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	        for (int i = 1, length = height - 1; i < length; i++) {
	            for (int k = 1, len = width - 1; k < len; k++) {
	                idx = 0;
	                for (int m = -1; m <= 1; m++) {
	                    for (int n = -1; n <= 1; n++) {
	                        pixColor = pixels[(i + m) * width + k + n];
	                        pixR = Color.red(pixColor);
	                        pixG = Color.green(pixColor);
	                        pixB = Color.blue(pixColor);

	                        newR = newR + (int) (pixR * gauss[idx]);
	                        newG = newG + (int) (pixG * gauss[idx]);
	                        newB = newB + (int) (pixB * gauss[idx]);
	                        idx++;
	                    }
	                }

	                newR /= delta;
	                newG /= delta;
	                newB /= delta;

	                newR = Math.min(255, Math.max(0, newR));
	                newG = Math.min(255, Math.max(0, newG));
	                newB = Math.min(255, Math.max(0, newB));

	                pixels[i * width + k] = Color.argb(255, newR, newG, newB);

	                newR = 0;
	                newG = 0;
	                newB = 0;
	            }
	        }

	        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	        return newBitmap;
	    }


	    public static Bitmap sunshine(Bitmap bitmap, int centerX, int centerY) {
	        final int width = bitmap.getWidth();
	        final int height = bitmap.getHeight();
	        Bitmap newBitmap = Bitmap.createBitmap(width, height,
	                Bitmap.Config.RGB_565);

	        int pixR = 0;
	        int pixG = 0;
	        int pixB = 0;

	        int pixColor = 0;

	        int newR = 0;
	        int newG = 0;
	        int newB = 0;
	        int radius = Math.min(centerX, centerY);

	        final float strength = 150F;
	        int[] pixels = new int[width * height];
	        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	        int pos = 0;
	        for (int i = 1, length = height - 1; i < length; i++) {
	            for (int k = 1, len = width - 1; k < len; k++) {
	                pos = i * width + k;
	                pixColor = pixels[pos];

	                pixR = Color.red(pixColor);
	                pixG = Color.green(pixColor);
	                pixB = Color.blue(pixColor);

	                newR = pixR;
	                newG = pixG;
	                newB = pixB;


	                int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(
	                        centerX - k, 2));
	                if (distance < radius * radius) {
	                    int result = (int) (strength * (1.0 - Math.sqrt(distance)
	                            / radius));
	                    newR = pixR + result;
	                    newG = pixG + result;
	                    newB = pixB + result;
	                }
	                newR = Math.min(255, Math.max(0, newR));
	                newG = Math.min(255, Math.max(0, newG));
	                newB = Math.min(255, Math.max(0, newB));
	                pixels[pos] = Color.argb(255, newR, newG, newB);
	            }
	        }

	        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	        return newBitmap;
	    }


	    public static Bitmap film(Bitmap bitmap) {
	        final int MAX_VALUE = 255;
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        Bitmap newBitmap = Bitmap.createBitmap(width, height,
	                Bitmap.Config.RGB_565);

	        int pixR = 0;
	        int pixG = 0;
	        int pixB = 0;

	        int pixColor = 0;

	        int newR = 0;
	        int newG = 0;
	        int newB = 0;

	        int[] pixels = new int[width * height];
	        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	        int pos = 0;
	        for (int i = 1, length = height - 1; i < length; i++) {
	            for (int k = 1, len = width - 1; k < len; k++) {
	                pos = i * width + k;
	                pixColor = pixels[pos];

	                pixR = Color.red(pixColor);
	                pixG = Color.green(pixColor);
	                pixB = Color.blue(pixColor);

	                newR = MAX_VALUE - pixR;
	                newG = MAX_VALUE - pixG;
	                newB = MAX_VALUE - pixB;

	                newR = Math.min(MAX_VALUE, Math.max(0, newR));
	                newG = Math.min(MAX_VALUE, Math.max(0, newG));
	                newB = Math.min(MAX_VALUE, Math.max(0, newB));

	                pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB);
	            }
	        }

	        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	        return newBitmap;
	    }


	    public static Bitmap sharpen(Bitmap bitmap) {
	        int[] laplacian = new int[]{-1, -1, -1, -1, 9, -1, -1, -1, -1};

	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        Bitmap newBitmap = Bitmap.createBitmap(width, height,
	                Bitmap.Config.RGB_565);

	        int pixR = 0;
	        int pixG = 0;
	        int pixB = 0;

	        int pixColor = 0;

	        int newR = 0;
	        int newG = 0;
	        int newB = 0;

	        int idx = 0;
	        float alpha = 0.3F;
	        int[] pixels = new int[width * height];
	        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	        for (int i = 1, length = height - 1; i < length; i++) {
	            for (int k = 1, len = width - 1; k < len; k++) {
	                idx = 0;
	                for (int m = -1; m <= 1; m++) {
	                    for (int n = -1; n <= 1; n++) {
	                        pixColor = pixels[(i + n) * width + k + m];
	                        pixR = Color.red(pixColor);
	                        pixG = Color.green(pixColor);
	                        pixB = Color.blue(pixColor);

	                        newR = newR + (int) (pixR * laplacian[idx] * alpha);
	                        newG = newG + (int) (pixG * laplacian[idx] * alpha);
	                        newB = newB + (int) (pixB * laplacian[idx] * alpha);
	                        idx++;
	                    }
	                }

	                newR = Math.min(255, Math.max(0, newR));
	                newG = Math.min(255, Math.max(0, newG));
	                newB = Math.min(255, Math.max(0, newB));

	                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
	                newR = 0;
	                newG = 0;
	                newB = 0;
	            }
	        }

	        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	        return newBitmap;
	    }


	    public static Bitmap emboss(Bitmap bitmap) {
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        Bitmap newBitmap = Bitmap.createBitmap(width, height,
	                Bitmap.Config.RGB_565);

	        int pixR = 0;
	        int pixG = 0;
	        int pixB = 0;

	        int pixColor = 0;

	        int newR = 0;
	        int newG = 0;
	        int newB = 0;

	        int[] pixels = new int[width * height];
	        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	        int pos = 0;
	        for (int i = 1, length = height - 1; i < length; i++) {
	            for (int k = 1, len = width - 1; k < len; k++) {
	                pos = i * width + k;
	                pixColor = pixels[pos];

	                pixR = Color.red(pixColor);
	                pixG = Color.green(pixColor);
	                pixB = Color.blue(pixColor);

	                pixColor = pixels[pos + 1];
	                newR = Color.red(pixColor) - pixR + 127;
	                newG = Color.green(pixColor) - pixG + 127;
	                newB = Color.blue(pixColor) - pixB + 127;

	                newR = Math.min(255, Math.max(0, newR));
	                newG = Math.min(255, Math.max(0, newG));
	                newB = Math.min(255, Math.max(0, newB));

	                pixels[pos] = Color.argb(255, newR, newG, newB);
	            }
	        }

	        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	        return newBitmap;
	    }

	    public static final byte[] yuvLandscapeToPortrait(byte[] sourceData,
	                                                      int width, int height) {
	        byte[] rotatedData = new byte[sourceData.length];
	        for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++)
	                rotatedData[x * height + height - y - 1] = sourceData[x + y
	                        * width];
	        }
	        return rotatedData;
	    }




	public static boolean saveBmpToSd(String dir, Bitmap bm, String filename,
									  int quantity, boolean recyle) {
		boolean ret = true;
		if (bm == null) {
			return false;
		}
		File dirPath = new File(dir);


		if (!exists(dir)) {
			dirPath.mkdirs();
		}


		if (!dir.endsWith(File.separator)) {
			dir += File.separator;
		}


		File file = new File(dir + filename);
		OutputStream outStream = null;
		try {
			file.createNewFile();
			outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, quantity, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ret = false;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			ret = false;
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (recyle && !bm.isRecycled()) {
				bm.recycle();
				bm = null;
				L.e("BitmaptoCard", "saveBmpToSd, recyle");
			}
		}
		return ret;
	}


	public static boolean exists(String url) {
		File file = new File(url);
		return file.exists();
	}

}

	
	
	

