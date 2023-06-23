package software.shattered.choir.persistence.tasks

class MainThreadRunStrategy : RunStrategy() {
    override fun execute(runnable: Runnable) {
        runnable.run()
    }
}