package com.ware.view.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import com.ware.common.BaseActivity
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val VERTEX_SHADER =
        "void main() {" +
                "gl_Position = vec4(0, 0.0, 0.0, 1.0);" +
                "gl_PointSize = 20.0;" +
                "}"
private const val FRAGMENT_SHADER =
        "void main() {" +
                "gl_FragColor = vec4(1., 0., 0.0, 1.0);" +
                "}"

class GlActivity : BaseActivity() {

    private var mProgram = -1

    private val mSurfaceView: GLSurfaceView by lazy { GLSurfaceView(this) }

    private val mRender = object : GLSurfaceView.Renderer {
        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            GLES20.glUseProgram(mProgram)

            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(0f, 0f, 0.5f, 1f)

            val vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            GLES20.glShaderSource(vShader, VERTEX_SHADER)
            GLES20.glCompileShader(vShader)

            val fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            GLES20.glShaderSource(fShader, FRAGMENT_SHADER)
            GLES20.glCompileShader(fShader)

            mProgram = GLES20.glCreateProgram()
            GLES20.glAttachShader(mProgram, vShader)
            GLES20.glAttachShader(mProgram, fShader)
            GLES20.glLinkProgram(mProgram)

            GLES20.glValidateProgram(mProgram)
            val status = intArrayOf(1)
            GLES20.glGetProgramiv(mProgram, GLES20.GL_VALIDATE_STATUS, status, 0)

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mSurfaceView)
        mSurfaceView.setEGLContextClientVersion(2)
        mSurfaceView.setRenderer(mRender)
        mSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onResume() {
        super.onResume()
        mSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mSurfaceView.onPause()
    }
}
