package org.nield.ch4_3

import org.ojalgo.optimisation.ExpressionsBasedModel
import org.ojalgo.optimisation.Variable
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger


// declare ojAlgo Model
val model = ExpressionsBasedModel()

// custom DSL for model expression inputs, eliminate naming and adding
val funcId = AtomicInteger(0)
val variableId = AtomicInteger(0)
fun variable() = Variable(variableId.incrementAndGet().toString().let { "Variable$it" }).apply(model::addVariable)
fun addExpression() = funcId.incrementAndGet().let { "Func$it"}.let { model.addExpression(it) }


fun main(args: Array<String>) {

    addSystemConstraints()
    Factory.values().forEach { it.addToModel() }

    val solution = model.maximise()
    println("Maximized profit: ${solution.value}")

    Factory.values().forEach { factory ->
        println("\r\nFactory ${factory.name} used ${factory.rawUsed} of raw material, generated ${factory.profitGenerated} of profit")
        factory.quantities.forEach {
            println("Factory ${factory.name} ${it.key} = ${it.value.value}")
        }
    }
}

val rawMaterial = 120 // kg


fun addSystemConstraints() {
    // allocation constraints
    addExpression().upper(rawMaterial).apply {

        Factory.values().asSequence()
                .flatMap { it.quantities.asSequence() }
                .forEach {
                    val product = it.key
                    val variable = it.value
                    set(variable, product.rawMaterial)
                }
    }
}


enum class Factory(val capacities: Map<Process,Int>, val rates: Map<PPKey, Int>) {
    A(capacities = mapOf(Process.GRINDING to 80, Process.POLISHING to 60),

        rates = mapOf(
                PPKey(Product.STANDARD, Process.GRINDING) to 4,
                PPKey(Product.STANDARD, Process.POLISHING) to 2,
                PPKey(Product.DELUXE, Process.GRINDING) to 2,
                PPKey(Product.DELUXE, Process.POLISHING) to 5
        )
    ),
    B(capacities = mapOf(Process.GRINDING to 60, Process.POLISHING to 75),

        rates = mapOf(
                PPKey(Product.STANDARD, Process.GRINDING) to 5,
                PPKey(Product.STANDARD, Process.POLISHING) to 5,
                PPKey(Product.DELUXE, Process.GRINDING) to 3,
                PPKey(Product.DELUXE, Process.POLISHING) to 6
        )
    );

    // quantity variables to be optimized for this factory, weighted with profit contribution, can't be negative
    val quantities = Product.values().asSequence()
            .map { it to variable().weight(it.profitContr).lower(0) }
            .toMap()

    val rawUsed get() = quantities.asSequence().map { it.key.rawMaterial * it.value.value.toDouble() }.sum()

    val profitGenerated get() = quantities.asSequence().map { it.key.profitContr * it.value.value.toDouble() }.sum()

    fun addToModel() {

        //process capacity constraints
        capacities.forEach {
            val process = it.key
            val processCapacity = it.value
            addExpression().upper(processCapacity).apply  {

                quantities.forEach {
                    val product = it.key
                    val variable = it.value

                    set(variable, rates[PPKey(product,process)])
                }
            }
        }
    }
}

enum class Product(val profitContr: Int, val rawMaterial: Int) {
    STANDARD(profitContr = 10, rawMaterial = 4),
    DELUXE(profitContr = 15, rawMaterial = 4)
}

enum class Process {
    GRINDING,
    POLISHING
}

data class PPKey(val product: Product, val process: Process)
