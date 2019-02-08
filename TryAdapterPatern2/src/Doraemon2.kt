enum class ToolType {
    TAKEKOPTER {
        override fun message() = "タケコプター！！！🚁"
    },
    DOKODEMODOOR {
        override fun message() = "どこでもドア！！！🚪"
    }; // セミコロンを忘れずに！

    abstract fun message():String
}

open class Doraemon2 {

    fun fetchTools(tool:ToolType):String {
        return tool.message()
    }
}