float PI = 3.1415926

int max(int a, int b) {
    if (a > b) return a
    return b
}

float max(float a, float b) {
    if (a > b) return a
    return b
}

int min(int a, int b) {
    if (a < b) return a
    return b
}

float min(float a, float b) {
    if (a < b) return a
    return b
}

int abs(int a) {
    if (a < 0) {
        a = 0-a
    }
    return a
}

float abs(float a) {
    if (a < 0) {
        a = 0-a
    }
    return a
}