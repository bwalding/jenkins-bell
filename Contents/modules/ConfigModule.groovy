// monitor configure

import groovy.transform.Field
import static Option.*
import groovy.xml.MarkupBuilder

@Field
Option<Map> config = none()

synchronized Option<Map> getConfig() {
    config = config.ifNoneThanSome{

//        def newConfig = defaultConfig()


        def slurper = onAModule.configFile("config.xml").defaultOrMap(null) {
            it.withReader("UTF-8") { reader ->
                def text = reader.text
                if(text)
                    new XmlSlurper().parseText(text)
                else null
            }
        }

        def newConfig = [:]
        onEachModule.readConfigElement(slurper, newConfig)
        newConfig
    }
}

synchronized void storeConfig(Map config) {

    config.buildConfigs = config.buildConfigs.findAll {it.name || it.job || it.server}

    onAModule.configFile("config.xml").ifSome {
        it.withWriter("UTF-8") { writer ->

            StringWriter sw = new StringWriter()
            def builder = new MarkupBuilder(sw)
            builder.config{
                onEachModule.writeConfigElement(builder, config)
            }


            writer << sw.toString()
        }
    }
    config = null

}