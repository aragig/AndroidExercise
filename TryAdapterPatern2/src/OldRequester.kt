class OldRequester: Human() {

    val doraemon = Doraemon()

    override fun wannaFly() {
        println(doraemon.fetchTakeKopter())
    }

    override fun wannaGoAnywhere() {
        println(doraemon.fetchDokodemoDoor())
    }
}
