# DataClassGenerate
_or simply DCG_


## Motivation
DataClassGenerate is a Kotlin compiler plugin that addresses APK size overhead.
DCG brings data class’ app size contribution on-par with semantically identical Plain Java Objects.
Kotlin positions data class as an easy-to-use and low friction utility for representing plain-old data.
Despite appearing compact in source code, the compiler generates multiple utility methods for each data class, which impact APK size e.g.: `hashCode`, `equals`, `toString`, `copy`, `componentN`.
Before an optimizer (like [Redex](https://github.com/facebook/redex/) or [R8](https://r8.googlesource.com/r8)) can strip these methods, it must prove that they are never used.
Since methods like `toString`, `equals`, and `hashCode` are so fundamental (defined on `Object`/`Any`), calls to them will exist all over the app, and the optimizer must prove that a data class would not flow into any of those call sites, which is often not possible.


## How it works
DataClassGenerate Kotlin Compiler Plugin processes `@DataClassGenerate` annotation.

### `@DataClassGenerate` ANNOTATION
_NOTE:_ DCG is applicable to Kotlin data classes only.

_NOTE:_ DCG configures an intent to generate (KEEP) or skip (OMIT) `toString`, `equals`, and `hashCode` methods generation.

_NOTE:_ DCG does not configure `copy`, `componentN`, and other methods, but [Redex](https://github.com/facebook/redex/) or R8 will take care of them.

`@DataClassGenerate` annotation configures Kotlin data class code generation and bytecode optimizations.
`@DataClassGenerate` is applicable only to Kotlin data classes, and will not make any change if applied to a regular class (*Unfortunately, Kotlin does not have a data class as an annotation target*).


`@DataClassGenerate` annotation, together with applied DataClassGenerate compiler plugin, does 3 things:
1. Configures code generation of `toString` method.
    - `@DataClassGenerate(toString = Mode.KEEP)` will generate `toString` as in a usual data class.
    - `@DataClassGenerate(toString = Mode.OMIT)` will NOT generate `toString` for an annotated data class.
1. Configures code generation of `equals` and `hashCode` methods.
    - `@DataClassGenerate(equalsHashCode = Mode.KEEP)` will generate `equals` and `hashCode` as in usual data class.
    - `@DataClassGenerate(equalsHashCode = Mode.OMIT)` will NOT generate `equals` and `hashCode` for an annotated data class.
1. Adds a marker super class `DataClassSuper` for suitable Data Classes to make them available for Redex [Class Merging Optimization](https://github.com/facebook/redex/blob/main/docs/passes.md#classmergingpass).
    - Do not upcast data classes to `DataClassSuper`. There is no guarantee a concrete data class will get a marker super class.


### Generation modes

How we can set `@DataClassGenerate (toString = ???, equalsHashCode = ???)` arguments?

|  Mode | Explanation |
|-------|-------|
| `KEEP` | Express an intent to keep/generate a method(s) |
| `OMIT` | Express an intent to omit method(s) generation. |

### Annotation defaults
`@DataClassGenerate` is declared with the following defaults:
 ```kotlin
 annotation class DataClassGenerate(
    val toString: Mode = Mode.OMIT,
    val equalsHashCode: Mode = Mode.KEEP
)
```

Kotlin allows annotation parameter name ommission. The table below explains `@DataClassGenerate` shortcuts:

|  Declaration | Explicit Equivalent |
|-------|-------|
|`@DataClassGenerate` | `@DataClassGenerate(toString = Mode.OMIT, equalsHashCode = Mode.KEEP)`|
|`@DataClassGenerate()` | `@DataClassGenerate(toString = Mode.OMIT, equalsHashCode = Mode.KEEP)`|
|`@DataClassGenerate(Mode.KEEP)` | `@DataClassGenerate(toString = Mode.KEEP, equalsHashCode = Mode.KEEP)`|
|`@DataClassGenerate(Mode.KEEP, Mode.OMIT)` | `@DataClassGenerate(toString = Mode.KEEP, equalsHashCode = Mode.OMIT)`|
|`@DataClassGenerate(equalsHashCode = Mode.OMIT)` | `@DataClassGenerate(toString = Mode.OMIT, equalsHashCode = Mode.OMIT)`|

## DCG  MODES
`DataClassGenerate` Kotlin compiler plugin works in multiple modes, but the most important is a `STRICT` mode.

### `STRICT` mode
In `STRICT` mode:
- Plugin applies only to data classes annotated with `@DataClassGenerate(...)`.
   - DCG will act following annotation instructions for method generation.
   - DCG will add marker super classes.

- Plugin reports a compilation error whenever it sees a data class without `@DataClassGenerate(...)` annotation.

Example:

```
// Plugin will generate:
// -`toString`
// -`equals`, and `hashCode`
@DataClassGenerate(toString = Mode.KEEP, equalsHashCode = Mode.KEEP)
data class A(val i:Int)

// Plugin will report a compilation error
data class B(val l:Long)
```

### `EXPLICIT` mode (current default)
In `EXPLICIT` mode:
- If a data class is annotated with `@DataClassGenerate`, DCG will act according to the annotation instructions for method generation.
- If data class is NOT annotated with @DataClassGenerate, DCG will only add marker super classes (read previous section for details).

Example:

```
// Plugin will generate:
// -`toString`
// -`equals`, and `hashCode`
@DataClassGenerate(toString = Mode.KEEP, equalsHashCode = Mode.KEEP)
data class A(val i:Int)

// Plugin will only create a marker super class
data class B(val l:Long)

// Plugin will do nothing
data class C(val l:Long): SomeSuperClass()
```


## Releases

Coming soon

## Public talks
- [FOSDEM'22. DataClassGenerate. Shrinking Kotlin data classes](https://archive.fosdem.org/2022/schedule/event/dataclassgenerate_shrinking_kotlin_data_classes/)
- [Droidcon'21. Kotlin Adoption at Scale](https://www.droidcon.com/2021/11/17/kotlin-adoption-at-scale/)

## Project team
DCG was created by the Kotlin Foundation team @ Meta:
- [Sergei Rybalkin](https://github.com/rybalkinsd/),
- [Adrian Catana](https://github.com/adicatana/),
- [Michal Zielinski](https://github.com/zielinskimz/),
- [Hui Qin Ng](https://github.com/nghuiqin/),
- [Nicola Corti](https://github.com/cortinico/)

