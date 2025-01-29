package com.example.multiplelayout

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CalculatorActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private var currentInput = ""
    private var operator = ""
    private var firstNumber = 0.0
    private var secondNumber = 0.0
    private var isNewInput = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calculator)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        display = findViewById(R.id.tv_display)

        setNumberClickListeners()
        setOperatorClickListeners()
        setFunctionClickListeners()
    }

    private fun playClickSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.button_click)
        mediaPlayer.start() // Play the sound immediately without waiting
        mediaPlayer.setOnCompletionListener {
            it.release() // Release the MediaPlayer when the sound is finished
        }
    }

    private fun triggerVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            // Vibrate for 50 milliseconds (you can adjust the duration)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                // For older versions, use the deprecated vibrate method
                vibrator.vibrate(50)
            }
        }
    }

    private fun setNumberClickListeners() {
        val numberIds = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7,
            R.id.btn_8, R.id.btn_9
        )

        val listener = { v: View ->
            val btn = v as Button
            if (isNewInput) {
                currentInput = ""
                isNewInput = false
            }
            currentInput += btn.text.toString()
            updateDisplay()
            playClickSound()  // Play sound when a number is pressed
            triggerVibration()  // Trigger vibration when a number is pressed
        }

        numberIds.forEach { id -> findViewById<Button>(id).setOnClickListener(listener) }
    }

    private fun setOperatorClickListeners() {
        val operatorIds = listOf(R.id.btn_add, R.id.btn_subtract, R.id.btn_multiply, R.id.btn_divide)

        val listener = { v: View ->
            val btn = v as Button
            if (currentInput.isNotEmpty()) {
                if (operator.isNotEmpty()) {
                    calculateResult()  // Calculate the result first if an operator is already set
                }
                firstNumber = currentInput.toDouble()
                operator = btn.text.toString()
                isNewInput = true
                updateDisplay()
                playClickSound()  // Play sound when an operator is pressed
                triggerVibration()  // Trigger vibration when an operator is pressed
            }
        }

        operatorIds.forEach { id -> findViewById<Button>(id).setOnClickListener(listener) }
    }

    private fun setFunctionClickListeners() {
        findViewById<Button>(R.id.btn_equals).setOnClickListener {
            calculateResult()
            playClickSound()  // Play sound when equals is pressed
            triggerVibration()  // Trigger vibration when equals is pressed
        }
        findViewById<Button>(R.id.btn_clear).setOnClickListener {
            clearDisplay()
            playClickSound()  // Play sound when clear is pressed
            triggerVibration()  // Trigger vibration when clear is pressed
        }
        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()  // Back button doesn't need sound or vibration
        }
    }

    private fun updateDisplay() {
        if (operator.isNotEmpty() && isNewInput) {
            display.text = "$firstNumber $operator"  // Show first number + operator
        } else {
            display.text = currentInput  // Show current input or second number
        }
    }

    private fun calculateResult() {
        if (currentInput.isEmpty() || operator.isEmpty()) return

        secondNumber = currentInput.toDouble()
        val result = when (operator) {
            "+" -> firstNumber + secondNumber
            "-" -> firstNumber - secondNumber
            "×" -> firstNumber * secondNumber
            "÷" -> if (secondNumber != 0.0) firstNumber / secondNumber else {
                display.text = "Error"
                return
            }
            else -> 0.0
        }

        display.text = result.toString()
        currentInput = result.toString()
        operator = ""
        isNewInput = true
    }

    private fun clearDisplay() {
        currentInput = ""
        operator = ""
        firstNumber = 0.0
        secondNumber = 0.0
        display.text = "0"
        isNewInput = true
    }
}