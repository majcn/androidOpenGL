package si.majcn.opengl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private GLSurfaceView mGLSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(new MyRenderer(this));
        setContentView(mGLSurfaceView);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mGLSurfaceView.onResume();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mGLSurfaceView.onPause();
    }

    
}
