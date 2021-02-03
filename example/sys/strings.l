import sys.math
import sys._builtin._string_

/*
* 字符串操作内置函数
*/

// 字符串s1是以字符串s2开始
boolean startsWith(string s1, string s2) {
    if (len(s1) < len(s2)) {
        return false
    }
    for (int i = 0;i < len(s2);i+=1) {
        if (strAt(s1, i) != strAt(s2, i)) {
            return false;
        }
    }
    return true
}

// 字符串s1是以字符串s2结束
boolean endsWith(string s1, string s2) {
    if (len(s1) < len(s2)) {
        return false
    }
    for (int i = len(s2) - 1, j = len(s1) - 1;i >= 0;i-=1, j-=1) {
        if (strAt(s1, j) != strAt(s2, i)) {
            return false;
        }
    }
    return true
}

// 将字符串s1中的s2替换成s3
string subString(string s1, int start, int end) {
    string newS = "";
    for (int i = start;i < min(len(s1), end);i+=1) {
        newS = newS + strAt(s1, i)
    }
    return newS
}

string subString(string s1, int start) {
    return subString(s1, start, len(s1))
}