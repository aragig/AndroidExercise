class Requester: Human() {

    val doraemon = Doraemon2()

    override fun wannaFly() {
        println(doraemon.fetchTools(ToolType.TAKEKOPTER))
    }

    override fun wannaGoAnywhere() {
        println(doraemon.fetchTools(ToolType.DOKODEMODOOR))
    }
}
