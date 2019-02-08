class Requester: Doraemon(), Human {
    override fun wannaFly() {
        println(fetchTakeKopter())
    }

    override fun wannaGoAnywhere() {
        println(fetchDokodemoDoor())
    }
}