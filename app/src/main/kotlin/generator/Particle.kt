package generator

class Particle(
    var position: Position,
    var velocity: Velocity,
    var bestPosition: Position
) {
    var fitness: Double = Double.NEGATIVE_INFINITY
    var bestFitness: Double = Double.NEGATIVE_INFINITY
}