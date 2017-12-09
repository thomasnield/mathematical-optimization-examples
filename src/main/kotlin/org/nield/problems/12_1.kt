package org.nield.problems

import org.ojalgo.optimisation.ExpressionsBasedModel
import org.ojalgo.optimisation.Variable
import java.time.Month
import java.util.concurrent.atomic.AtomicInteger


// declare ojAlgo Model
val model = ExpressionsBasedModel()

// custom DSL for model expression inputs, eliminate naming and adding
val funcId = AtomicInteger(0)
val variableId = AtomicInteger(0)
fun variable() = Variable(variableId.incrementAndGet().toString().let { "Variable$it" }).apply(model::addVariable)
fun addExpression() = funcId.incrementAndGet().let { "Func$it"}.let { model.addExpression(it) }



enum class Oil(val isVegetable: Boolean, val hardness: Double) {
    VEG1(true, 8.8),
    VEG2(true, 6.1),
    OIL1(false, 2.0),
    OIL2(false, 4.2),
    OIL3(false, 5.0)
}

val finalProductPrice = 150
val storageCapacity = 1000
val rawStorageRate = 5 // per ton per month

val vegProcessingLimit = 200
val nonVegProcessingLimit = 250

val finalProductHardnessLimit = 3.0..6.0

data class ProductionPlan(val month: Month, val oil: Oil, val price: Int) {
    val quantity = variable().lower(0)
}


val prices = listOf(
        ProductionPlan(Month.JANUARY, Oil.VEG1, 110),
        ProductionPlan(Month.JANUARY, Oil.VEG2, 120),
        ProductionPlan(Month.JANUARY, Oil.OIL1, 130),
        ProductionPlan(Month.JANUARY, Oil.OIL2, 110),
        ProductionPlan(Month.JANUARY, Oil.OIL3, 115),

        ProductionPlan(Month.FEBRUARY, Oil.VEG1, 130),
        ProductionPlan(Month.FEBRUARY, Oil.VEG2, 130),
        ProductionPlan(Month.FEBRUARY, Oil.OIL1, 110),
        ProductionPlan(Month.FEBRUARY, Oil.OIL2, 90),
        ProductionPlan(Month.FEBRUARY, Oil.OIL3, 115),

        ProductionPlan(Month.MARCH, Oil.VEG1, 110),
        ProductionPlan(Month.MARCH, Oil.VEG2, 140),
        ProductionPlan(Month.MARCH, Oil.OIL1, 130),
        ProductionPlan(Month.MARCH, Oil.OIL2, 100),
        ProductionPlan(Month.MARCH, Oil.OIL3, 95),

        ProductionPlan(Month.APRIL, Oil.VEG1, 120),
        ProductionPlan(Month.APRIL, Oil.VEG2, 110),
        ProductionPlan(Month.APRIL, Oil.OIL1, 120),
        ProductionPlan(Month.APRIL, Oil.OIL2, 120),
        ProductionPlan(Month.APRIL, Oil.OIL3, 125),

        ProductionPlan(Month.MAY, Oil.VEG1, 100),
        ProductionPlan(Month.MAY, Oil.VEG2, 120),
        ProductionPlan(Month.MAY, Oil.OIL1, 150),
        ProductionPlan(Month.MAY, Oil.OIL2, 110),
        ProductionPlan(Month.MAY, Oil.OIL3, 105),

        ProductionPlan(Month.JUNE, Oil.VEG1, 90),
        ProductionPlan(Month.JUNE, Oil.VEG2, 100),
        ProductionPlan(Month.JUNE, Oil.OIL1, 140),
        ProductionPlan(Month.JUNE, Oil.OIL2, 80),
        ProductionPlan(Month.JUNE, Oil.OIL3, 135)
)
