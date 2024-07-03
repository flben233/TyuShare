package util

class ProcessUtil {
    companion object {
        private fun kill(processHandle: ProcessHandle) {
            processHandle.descendants().forEach {
                kill(it)
            }
            processHandle.destroyForcibly()
        }

        fun killProcess(process: Process) {
            kill(process.toHandle())
        }

        fun startProcess(vararg commend: String): Process {
            return ProcessBuilder(*commend).start()
        }
    }
}