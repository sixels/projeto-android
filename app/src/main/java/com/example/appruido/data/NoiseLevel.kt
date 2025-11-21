package com.example.appruido.data

enum class NoiseLevel(val label: String) {
    Low("baixo"), // 0 a 35
    Medium("moderado"), // 35 a 65
    High("perigo"), // 65 a 100
    Extreme("extremo"), // acima de 100
}

fun getNoiseLevel(decibels: Double): NoiseLevel {
    return when {
        decibels < 35 -> NoiseLevel.Low
        decibels < 65 -> NoiseLevel.Medium
        decibels < 100 -> NoiseLevel.High
        else -> NoiseLevel.Extreme
    }
}
