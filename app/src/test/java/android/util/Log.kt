package android.util

class Log private constructor() {
    companion object {
        const val DEBUG = 3
        const val ERROR = 6
        const val INFO = 4
        const val VERBOSE = 2
        const val WARN = 5

        @JvmStatic fun d(tag: String?, msg: String?): Int = 0
        @JvmStatic fun e(tag: String?, msg: String?): Int = 0
        @JvmStatic fun i(tag: String?, msg: String?): Int = 0
        @JvmStatic fun v(tag: String?, msg: String?): Int = 0
        @JvmStatic fun w(tag: String?, msg: String?): Int = 0
        @JvmStatic fun e(tag: String?, msg: String?, tr: Throwable?): Int = 0
    }
}