package com.example.texttospeech

import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val reqCode = 1
    private lateinit var tts: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private var fabStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), reqCode)
        }

        tts = TextToSpeech(this) { status ->
            if(status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
            } else
                Log.d("TAG", "Initialization failed")
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        ibtnSpeak.setOnClickListener {
            speak()
        }

        fabAns.setOnClickListener {
            setDrawable()
            action()
            fabStatus = !fabStatus
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(p0: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d("TAG", "end of speech")
            }

            override fun onError(p0: Int) {
                Log.d("TAG", "err $p0")
            }

            override fun onResults(p0: Bundle?) {
                val speech = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if(!speech.isNullOrEmpty()) {
                    if(speech[0].equals("4")) {
                        tts.setPitch(1f)
                        tts.setSpeechRate(0.8f)
                        tts.speak("correct", TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                    else {
                        tts.setPitch(1f)
                        tts.setSpeechRate(0.8f)
                        tts.speak("incorrect", TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
            }

            override fun onPartialResults(p0: Bundle?) {
                Log.d("TAG", "partial res")
            }

            override fun onEvent(p0: Int, p1: Bundle?) {}
        })

    }

    private fun action() {
        if(!fabStatus) speechRecognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
        else speechRecognizer.stopListening()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDrawable() {
        if(!fabStatus) fabAns.setImageDrawable(getDrawable(R.drawable.ic_mic_on))
        else fabAns.setImageDrawable(getDrawable(R.drawable.ic_mic_off))
    }

    private fun speak() {
        val text: CharSequence = "two times two equals what"
        tts.setPitch(1f)
        tts.setSpeechRate(0.8f)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == reqCode) {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(this, "Please grant record audio permission.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}