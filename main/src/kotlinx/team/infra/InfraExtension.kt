package kotlinx.team.infra

import groovy.lang.*
import org.gradle.api.*
import org.gradle.util.*

open class InfraExtension(val project: Project) {
    val publishing = PublishingConfiguration()
    private var publishingHandler: ((PublishingConfiguration) -> Unit)? = null
    internal fun afterPublishing(handler: (PublishingConfiguration) -> Unit) {
        publishingHandler = handler
    }

    fun publishing(configureClosure: Closure<PublishingConfiguration>) {
        ConfigureUtil.configureSelf(configureClosure, publishing)
        publishingHandler?.invoke(publishing)
    }    
    
    
    val node = NodeConfiguration()
    private var nodeHandler: ((NodeConfiguration) -> Unit)? = null
    internal fun afterNode(handler: (NodeConfiguration) -> Unit) {
        nodeHandler = handler
    }

    fun node(configureClosure: Closure<NodeConfiguration>) {
        ConfigureUtil.configureSelf(configureClosure, node)
        nodeHandler?.invoke(node)
    }
}


