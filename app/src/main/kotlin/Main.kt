import generator.Particle
import generator.Position
import generator.Swarm
import generator.Velocity
import kotlin.math.hypot

// use docker exec -it <docker id> ./gradlew run
fun main() {
    val gridWidth = 50
    val gridHeight = 20
    val particleCount = 20
    val goalPosition = Position(gridWidth / 2, gridHeight / 2)
    val swarm = initializeSwarm(particleCount, gridWidth, gridHeight)

    repeat(50) { iteration ->
        clearScreen()
        println("Iteration: ${iteration + 1}")
        updateParticles(swarm, gridWidth, gridHeight, goalPosition)
        printGrid(gridWidth, gridHeight, swarm, goalPosition)
    }
}

const val INERTIA_WEIGHT = 0.5
const val COGNITIVE_CONSTANT = 1.5
const val SOCIAL_CONSTANT = 1.5

fun calculateFitness(position: Position, goal: Position): Double {
    val dx = (goal.x - position.x).toDouble()
    val dy = (goal.y - position.y).toDouble()
    return -hypot(dx, dy)
}

fun initializeSwarm(
    particleCount: Int,
    gridWidth: Int,
    gridHeight: Int
): Swarm {
    val particles = mutableListOf<Particle>()
    for (i in 0 until particleCount) {
        val position = Position((0 until gridWidth).random(), (0 until gridHeight).random())
        val velocity = Velocity(0.0, 0.0)
        val particle = Particle(position, velocity, position.copy())
        particles.add(particle)
    }

    val globalBestPosition = particles[0].position.copy()
    return Swarm(particles, globalBestPosition)
}

fun updateParticles(swarm: Swarm, gridWidth: Int, gridHeight: Int, goal: Position) {
    swarm.particles.forEach { particle ->
        // do a fitness update
        particle.fitness = calculateFitness(particle.position, goal)

        // do a personal best update
        if (particle.fitness > particle.bestFitness) {
            particle.bestFitness = particle.fitness
            particle.bestPosition = particle.position.copy()
        }

        // do a global best update
        if (particle.fitness > swarm.globalBestFitness) {
            swarm.globalBestFitness = particle.fitness
            swarm.globalBestPosition = particle.position.copy()
        }

        // random coefficients
        val r1 = Math.random()
        val r2 = Math.random()

        // velocity update
        particle.velocity.vx = INERTIA_WEIGHT * particle.velocity.vx +
                COGNITIVE_CONSTANT * r1 * (particle.bestPosition.x - particle.position.x) +
                SOCIAL_CONSTANT * r2 * (swarm.globalBestPosition.x - particle.position.x)

        particle.velocity.vy = INERTIA_WEIGHT * particle.velocity.vy +
                COGNITIVE_CONSTANT * r1 * (particle.bestPosition.y - particle.position.y) +
                SOCIAL_CONSTANT * r2 * (swarm.globalBestPosition.y - particle.position.y)

        // position update
        particle.position.x += particle.velocity.vx.toInt()
        particle.position.y += particle.velocity.vy.toInt()

        // boundary conditions
        particle.position.x = particle.position.x.coerceIn(0, gridWidth - 1)
        particle.position.y = particle.position.y.coerceIn(0, gridHeight - 1)
    }
}

fun clearScreen() {
    print("\u001b[H\u001b[2J")
    System.out.flush()
}

fun printGrid(
    gridWidth: Int,
    gridHeight: Int,
    swarm: Swarm,
    goal: Position
) {
    val grid = Array(gridHeight) { Array(gridWidth) { '.' } }

    // PUT THEM ON THE BOARD!
    swarm.particles.forEach { particle ->
        grid[particle.position.y][particle.position.x] = '*'
    }

    // place goal
    grid[goal.y][goal.x] = 'G'

    // output top border
    println("#".repeat(gridWidth + 2))

    // output grid with side borders
    for (row in grid) {
        print("#")
        for (cell in row) {
            print(cell)
        }
        println("#")
    }

    // output bottom border
    println("#".repeat(gridWidth + 2))

    // output global best fitness
    println("Global Best Fitness: ${"%.2f".format(swarm.globalBestFitness)}")
}