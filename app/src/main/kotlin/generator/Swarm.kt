package generator

class Swarm(
    val particles: List<Particle>,
    var globalBestPosition: Position,
    var globalBestFitness: Double = Double.NEGATIVE_INFINITY
)