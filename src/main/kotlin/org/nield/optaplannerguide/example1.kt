package org.nield.optaplannerguide

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.domain.variable.PlanningVariable
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.api.solver.SolverFactory

data class CloudComputer(val cpuPower: Int, val memory: Int, val networkBandwidth: Int, val cost: Int)

@PlanningEntity
data class CloudProcess(val requiredCpuPower: Int, val requiredMemory: Int, val requiredNetworkBandwidth: Int) {

    @PlanningVariable(valueRangeProviderRefs = arrayOf("computerRange"))
    var cloudComputer: CloudComputer? = null

}

// just get this to compile and run, start simple
@PlanningSolution
class CloudBalance {

    @ValueRangeProvider(id = "computerRange")
    @ProblemFactCollectionProperty
    val computerList = listOf(
            CloudComputer(200,200,200,200)
    )

    @PlanningEntityCollectionProperty
    val processList = listOf(
            CloudProcess(200,200,200)
    )

    @PlanningScore
    var score: HardSoftScore? = null
}

fun main(args: Array<String>) {
    val solverfactory: SolverFactory<CloudBalance> = SolverFactory.createEmpty()

    val solver = solverfactory.buildSolver()

    val unsolvedCloudBalance = CloudBalance()

    val solvedCloudBalance = solver.solve(unsolvedCloudBalance)

    println("\nSolved cloudBalance: ${solvedCloudBalance.score}")
}