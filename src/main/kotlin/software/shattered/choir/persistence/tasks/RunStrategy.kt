package software.shattered.choir.persistence.tasks

abstract class RunStrategy {
    abstract fun execute(runnable: Runnable)
}