package org.nield


import org.ojalgo.optimisation.ExpressionsBasedModel
import org.ojalgo.optimisation.Variable
import java.util.concurrent.atomic.AtomicInteger


// declare ojAlgo Model
val model = ExpressionsBasedModel()

// custom DSL for model expression inputs, eliminate naming and adding
val funcId = AtomicInteger(0)
val variableId = AtomicInteger(0)
fun variable() = Variable(variableId.incrementAndGet().toString().let { "Variable$it" }).apply(model::addVariable)
fun addExpression() = funcId.incrementAndGet().let { "Func$it"}.let { model.addExpression(it) }


fun main(args: Array<String>) {
	println("Hello world!")
}

val rawMaterial = 120 // kg

enum class Factory(val rawAllocation: Int? = null, val capacities: Map<Process,Int>, val rates: Map<PPKey, Int>) {
    A(capacities = mapOf(Process.GRINDING to 80, Process.POLISHING to 60),
        rates = mapOf(
                PPKey(Product.STANDARD, Process.GRINDING) to 4,
                PPKey(Product.STANDARD, Process.POLISHING) to 2,
                PPKey(Product.DELUXE, Process.GRINDING) to 2,
                PPKey(Product.DELUXE, Process.POLISHING) to 5
        ),
        rawAllocation = 75
    ),
    B(capacities = mapOf(Process.GRINDING to 60, Process.POLISHING to 75),
        rates = mapOf(
                PPKey(Product.STANDARD, Process.GRINDING) to 5,
                PPKey(Product.STANDARD, Process.POLISHING) to 5,
                PPKey(Product.DELUXE, Process.GRINDING) to 3,
                PPKey(Product.DELUXE, Process.POLISHING) to 6
        ),
        rawAllocation = 45
    );

    val quantities = Product.values().asSequence()
            .map { it to variable().weight(it.profitContr).lower(0) }
            .toMap()

    fun addToModel() {

        /**
         * R = raw rate
         * A = Allocated raw
         *
         * R1X1 + R2
         */
        quantities.forEach {
            addExpression()
                    .lower(rawAllocation)
                    .set(it.value, it.key.rawMaterial)
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
