package si.majcn.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.learnopengles.android.common.RawResourceReader;

public class MyRenderer implements GLSurfaceView.Renderer {
	
	private Context mContext;
	
	private final FloatBuffer mTriangleVertices;
	private final FloatBuffer mTriangleColor;
	private final ShortBuffer mTriangleIndices;
	
	private float[] mViewMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

	private int mMVPMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;
	
	public MyRenderer(Context context) {
		final float[] triangleVerticesData = {
	            -0.5f, -0.5f, 0.5f,
	            0.5f, -0.5f, 0.5f,
	            0.0f, -0.5f, -0.5f,
	            0.0f, 0.5f, 0.0f
        };
		
		final float[] triangleColorData = {
				1.0f, 0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f, 1.0f,
				0.0f, 1.0f, 0.0f, 1.0f
		};
		
		final short[] triangleIndicesData = {
				0, 1, 3,
				0, 2, 1,
				0, 3, 2,
				1, 2, 3
		};
		
		mContext = context;
		
		mTriangleVertices = ByteBuffer.allocateDirect(triangleVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices.put(triangleVerticesData).position(0);
		
		mTriangleColor = ByteBuffer.allocateDirect(triangleColorData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleColor.put(triangleColorData).position(0);
		
		mTriangleIndices = ByteBuffer.allocateDirect(triangleIndicesData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		mTriangleIndices.put(triangleIndicesData).position(0);
	}

	public void onDrawFrame(GL10 glUnused) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.setLookAtM(mViewMatrix, 0, 1.0f, 1.0f, 1.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		mTriangleVertices.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mTriangleVertices);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		
		mTriangleColor.position(0);
		GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, mTriangleColor);
		GLES20.glEnableVertexAttribArray(mColorHandle);
		
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 12, GLES20.GL_UNSIGNED_SHORT, mTriangleIndices);
	}

	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		final float ratio = (float) width / height;
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 10.0f);
	}

	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		int vertexShaderHandler = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(vertexShaderHandler, RawResourceReader.readTextFileFromRawResource(mContext, R.raw.vertexshader));
		GLES20.glCompileShader(vertexShaderHandler);
		
		int fragmentShaderHandler = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(fragmentShaderHandler, RawResourceReader.readTextFileFromRawResource(mContext, R.raw.fragmentshader));
		GLES20.glCompileShader(fragmentShaderHandler);
		
		int programHandle = GLES20.glCreateProgram();
		GLES20.glAttachShader(programHandle, vertexShaderHandler);
		GLES20.glAttachShader(programHandle, fragmentShaderHandler);
		GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
		GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
		GLES20.glLinkProgram(programHandle);
		
		mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
		mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
		
		GLES20.glUseProgram(programHandle);
	}

}
