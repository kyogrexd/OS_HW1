package com.example.os_hw1

import android.opengl.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.example.os_hw1.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.math.log
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val rowA = 50
        val columnA = 80
        val matrixA = Array(rowA) { DoubleArray(columnA) }

        val rowB = 80
        val columnB = 50
        val matrixB = Array(rowB) { DoubleArray(columnB) }

        for (i in 0 until rowA) {
            for (j in 0 until columnA) {
                matrixA[i][j] = 6.6 * (i + 1) - 3.3 * (j + 1)
            }
        }

        for (i in 0 until rowB) {
            for (j in 0 until columnB) {
                matrixB[i][j] = 100 + 2.2 * (i + 1) - 5.5 * (j + 1)
            }
        }

        binding.textView.setOnClickListener {
            val product = Array(rowA) { DoubleArray(columnB) }

            val timeCost = measureTimeMillis {
                for (i in 0 until rowA) {
                    for (j in 0 until columnB) {
                        for (k in 0 until columnA) {
                            product[i][j] += matrixA[i][k] * matrixB[k][j]
                        }
                    }
                }
            }
            Log.e("for-loop", "執行時間:$timeCost ms")

            val product2 = Array(rowA) { DoubleArray(columnB) }

            runBlocking {
                val timeCost2 = measureTimeMillis {
                    for (i in 0 until rowA) {
                        withContext(Dispatchers.IO) {
                            for (j in 0 until columnB) {
                                for (k in 0 until columnA) {
                                    product2[i][j] += matrixA[i][k] * matrixB[k][j]
                                }
                            }
                        }
                    }
                }
                Log.e("50 threads", "執行時間:$timeCost2 ms")
            }

            val product3 = Array(rowA) { DoubleArray(columnB) }
            runBlocking {
                val timeCost3 = measureTimeMillis {
                    for (a in 0 until 5) {
                        for (b in 0 until 2) {
                            withContext(Dispatchers.IO) {
                                for (i in 0 until rowA / 5) { //10
                                    for (j in 0 until columnB / 2) { //25
                                        for (k in 0 until columnA) { //80
                                            product3[a * 10 + i][b * 25 + j] += matrixA[a * 10 + i][k] * matrixB[k][b * 25 + j]
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Log.e("10 threads", "執行時間:$timeCost3 ms")
            }
        }
    }
}