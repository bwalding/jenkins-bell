/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 30/08/2012
 * Time: 07:25
 * To change this template use File | Settings | File Templates.
 */

import static Option.*

class Kernel {
    GroovyShell shell = new GroovyShell(Kernel.class.getClassLoader())
    private List<File> moduleFiles;

    List<Module> moduleList = []

    //def required = new ServiceProxy(this.&handleRequired)
    def onAModule = new ServiceProxy(this.&handleOptional)
    def onEachModule = new ServiceProxy(this.&handleEach)

    Kernel(List<File> moduleFiles){
        this.moduleFiles = moduleFiles
    }

    void reload(){
        shell.setVariable("kernel", this)
        shell.setVariable("onAModule", onAModule)
        shell.setVariable("onEachModule", onEachModule)


        def allModules = moduleFiles.findAll {it.isFile()}.collect {
            println("parsing $it")
            new Module(script: shell.parse(it), file: it)
        }

        allModules.each {
            if(it.script.run() == Boolean.FALSE) {
                println "$it.file.name ignored"

            } else {
                println "$it.file.name started"
                moduleList << it
            }
        }

        onEachModule.start()

    }

//    private def handleRequired(String name, Object args) {
//        def argTypes = args.collect {it.getClass()}
//        def onEachModule = findRespondingModules(name, argTypes)
//        def call = "$name'(${argTypes.join(', ')})"
//        assert onEachModule, "No onAModule found for method '$call'"
//        def result = onEachModule.head().script.invokeMethod(name, args)
//        if(result instanceof Option){
//            assert result.isSome(), "No result for method '$call'"
//            return result.value
//        }
//    }

    private def handleOptional(String name, Object args) {
        def modules = findRespondingModules(name, args.collect {it.getClass()})
        def results = modules.take(1).collect {
            def methodResult = it.script.invokeMethod(name, args)
            methodResult
        }
        def option = option(results)
        def flattenOption = option.flatten()
        flattenOption
    }

    private def handleEach(String name, Object args) {
        def modules = findRespondingModules(name, args.collect {it.getClass()})
        modules.collect {
            it.script.invokeMethod(name, args)
        }.findAll {it?.isSome()}.collect {it.value}
    }


    private List<Module> findRespondingModules(name, argTypes) {
        moduleList.findAll {
            it.script.respondsTo(name, argTypes.toArray())
        }
    }

    class Module {
        Script script
        File file
    }

}
