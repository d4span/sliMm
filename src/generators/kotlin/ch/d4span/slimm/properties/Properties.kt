package ch.d4span.slimm.properties

import kotlin.random.Random

typealias Seed = Int
typealias Outcome = Result<Unit>
typealias Generator<T> = (Random) -> T

fun <S, T> Generator<S>.map(f: (S) -> T): Generator<T> = { r: Random -> f(this(r)) }
fun <S, T> Generator<S>.pair(gen: Generator<T>): Generator<Pair<S, T>> =
        { r: Random -> this(r) to gen(r) }

typealias Property<T> = (T) -> Outcome

fun <T : Any> forAll(gen: Generator<T>, seed: Seed = 1): Pair<Seed, Sequence<T>> {
    val random = Random(seed)
    return seed to generateSequence { gen(random) }
}

fun <T> Pair<Seed, Sequence<T>>.check(prop: Property<T>, size: Int = 100): Pair<Seed, Sequence<Pair<T, Outcome>>> =
        this.first to this.second.map { it to prop(it) }.take(size)

fun <T> Pair<Seed, Sequence<Pair<T, Outcome>>>.verify(): Boolean =
        this.second.filter { it.second.isFailure }.none()

fun <T> Pair<Seed, Sequence<Pair<T, Outcome>>>.failures(): Pair<Seed, Sequence<Pair<T, Outcome>>> =
        this.first to this.second.filter { it.second.isFailure }