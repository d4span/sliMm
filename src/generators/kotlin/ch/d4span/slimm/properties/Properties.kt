package ch.d4span.slimm.properties

import kotlin.random.Random

typealias Seed = Int
typealias Outcome = Result<Unit>
typealias Generator<T> = (Random) -> T
typealias Property<T> = (T) -> Outcome

fun <T> forAll(gen: Generator<T>, seed: Seed = 100): Pair<Seed, Sequence<T>> {
    val random = Random(seed)
    return seed to generateSequence { gen(random) }
}

fun <T> Pair<Seed, Sequence<T>>.check(prop: Property<T>, size: Int = 100): Pair<Seed, Sequence<Pair<T, Outcome>>> =
        this.first to this.second.map { it to prop(it) }.take(size)

fun <T> Pair<Seed, Sequence<Pair<T, Outcome>>>.verify(): Boolean =
        this.second.filter { it.second.isFailure }.none()

fun <T> Pair<Seed, Sequence<Pair<T, Outcome>>>.failures() =
        this.first to this.second.filter { it.second.isFailure }