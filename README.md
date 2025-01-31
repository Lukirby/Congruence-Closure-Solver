# Congruence Closure Solver

This project is a Congruence Closure Solver that processes logical expressions or set of literals and determines their satisfiability. 
The solver supports various options such as recursive find, heuristic union, and forbidden set.

## Prerequisites

- Java Development Kit (JDK) 8 or later
- A terminal or command prompt
- No external libraries are required

## Bash Files

There are two bash files in the main directory:

1. **build\_project.bash**: Use this script to compile the project.
2. **run\_solver.bash**: Use this script to run the solver.

The compilation is optional since the project is already compiled and ready to run.

## Using the Solver

When using the solver, you need to specify the name of the file as input. Depending on the file extension, the file should be placed in the corresponding directory:

- **.txt** files: Place them in the `input` folder.
- **.properties** files: Place them in the `generator` folder.
- **.smt2** files: Place them in the `smtb_input` folder.

Additionally, you can specify options alongside the file name:

- **`-v`**: Enables verbose mode.
- **`-r`**: Uses the recursive version of the FIND function.
- **`-e`**: Enables the heuristic version of the UNION function.
- **`-f`**: Enables the forbidden set.

Ensure that the correct file path, extension, and options are provided when running the solver.

### Example Execution

#### Command to Run the Solver:

```bash
./run_solver.bash
```

#### Input and Output Example:

Input prompt:

```
Please, enter the file name in input folder in order to analyze:
example.txt -v -r -e
```


## Syntax of Txt Files

The solver uses the following syntax conventions:

- **`=`**: Used for equalities.
- **`!=`**: Used for inequalities.
- **`;`**: Used to separate equalities and disequalities.
- **`_`**: Used only for comments.
- **`°`**: Prohibited and used exclusively to indicate the constant of truth.
- **`&`**: Represents a logical AND.
- **`|`**: Represents a logical OR.
- **`->`** or **`£`**: Represents a logical implication.
- **`<->`** or **`$`**: Represents a logical bi-implication (coimplication).
- **`~`**: Represents a logical NOT.
- **`()`**: Parentheses are used to indicate functions or predicates with arguments separated by commas. Functions and predicates must always be written with parentheses and are always in prefix notation (never infix or postfix).
- **Functions vs Predicates**: Functions always appear within equalities (e.g., `f(a, b) = c`), while predicates never do (e.g., `P(a, b)`).
- **`,`**: Used to separate arguments in functions or predicates.
- **`[]`**: Brackets are used to group logical connectives for precedence.
- **Whitespace**: Whitespace is not significant and will be ignored during processing.
- **`\exists x.`**: Denotes "there exists an x." The dot (`.`) is mandatory to indicate the end of the variable.
- **`\forall x.`**: Denotes "for all x." The dot (`.`) is mandatory to indicate the end of the variable.

Mismatched parentheses, brackets, or commas will raise errors.

### Types of Formulas

There are two types of formulas that can be submitted:

1. **Logical Formula with Connectives**: A formula that uses logical connectives without the use of `;`.
2. **Sequence of Equalities, Disequalities, or Predicates**: A sequence separated by `;` and without logical connectives.

Ensure that the formulas adhere to the specified syntax conventions for correct processing.

### Processing

- The existential and universal quantifiers will be dropped during processing.
- Each formula if in "connective" form will be processed in DNF form, and each cube will be processed at a time as a set of equalities and disequalities.
- Each (negative) predicate will be processe as a function (not) equal to the constant of truth. Example `P(a,b); ~R` will be processed as `f_P(a,b) = °; f_R != °`.


### Syntax Example

1. **Using equalities, inequalities, and separators**:

   - Example: `a = b; c != d; e = f`

2. **Using parentheses for functions and predicates**: `function(arg1, arg2, arg3)`

   - Functions Example: `f(a, b) = c;`
   - Predicates Example: `P(a, b);`

3. **Using brackets for precedence**:

   - Example: `[A & B] -> [C | ~D]`
   - Example: `[[P(a, b) -> Q(a)] & [~R]] | S`

4. **Using quantifiers**:

   - Example: `\exists x. [P(x) & Q(x)]`
   - Example: `\forall y. [~P(y) | R(y)]`

5. **Type of formula**:

   - Example: `A & [b = e] | C(a,b) -> [d != f]`
   - Example: `a = b; c != d; P(a, b); ~Q(c, d)`


### Txt Output

From the input file named `example.txt`, the solver will generate an output file named `example.txt` in the `output` folder. 

## Syntax of Properties Files

A properties file is a simple key-value pair file used to store configuration data. Each line in the file represents a property, with the key and value separated by an equals sign (`=`). Properties files are commonly used in Java applications to manage application settings and configurations.

Example of a properties file:

```properties
# This is a comment
key1=value1
key2=value2
key3=value3
```

In the context of the generator, the properties file defines various parameters that control the behavior of the generator, such as the list of constants, functions, predicates, and other settings.

The properties file should be placed in the `generator` folder and have a `.properties` extension, and have 
the following keys:

- `constants`: A comma-separated list of constants used in the generator. Example: `a,b`
- `functions`: A comma-separated list of functions used in the generator. Example: `f,g`
- `predicates`: A comma-separated list of predicates used in the generator. Example: `P,Q`
- `maxDepth`: An integer representing the maximum depth of generated expressions. Example: `5`
- `maxArity`: An integer representing the maximum arity (number of arguments) of functions and predicates. Example: `5`
- `cubes`: An integer representing the number of cubes to generate. Example: `100`
- `probabilityConstants`: A floating-point number between 0 and 1 representing the probability of using constants. Example: `0`
- `probabilityFunctions`: A floating-point number between 0 and 1 representing the probability of using functions. Example: `0.5`
- `probabilityPredicates`: A floating-point number between 0 and 1 representing the probability of using predicates. Example: `0.5`
- `seed`: (optional) An integer used to initialize the random number generator for reproducibility. Example: `0`

The generator that use a file with the name `example.properties` will generate a file with the name `example_seed.txt` in the `input` folder,
and the output will be generated in the output folder with the name `example_seed.txt`.
Where `seed` is the seed used to generate the file.

Hints:
- The `seed` key is optional. If not provided, the generator will use the current time as the seed.
- If the probabilities do not sum to 1, then a they will be normalized to sum to 1.
- If a probability is set to 0, then the corresponding elements (constants, functions, or predicates) will not be used in the generation.
- The `maxDepth`,`maxArity` and `cubes` keys control the complexity of the generated expressions. Higher values will result in more complex expressions and exausts the memory of the computer.
- The `constants`, `functions`, and `predicates` keys should be non-empty lists of identifiers separated by commas.
- To make a symbol more likely to be used, just add it more times in the list. For example `a,a,a,b` will make `a` more likely to be used than `b`.

Other examples of properties files can be found in the `generator` folder.

## Syntax of SMT2 Files

The SMT2 format is a standard format for Satisfiability Modulo Theories (SMT) problems. 
Since the the language is complex, the solver only supports a subset of the language, in particular only the following subfolder: QU_UF -> eq_diamond.
In practice, only the files with only one `assert` command and only keywords like `and`, `or`, `not`, `=` are supported.

From a file named `example.smt2` in the `smtb_input` folder, the solver will generate an input file named `example.txt` in the `input` folder and an output file named `example.txt` in the `output` folder. 


## Theories

The solver supports the following theories:
- Equality
- List
- Array withoout extensionality

To make the parser to use a theory, this syntax should be used in the input file:
- For the Thory of List, at least one of the following: `cons(...,...)`, `car(...)`, `cdr(...)`, `atom(...)`, `~atom(...)`.
- For the Theory of Array, at least one of the following: `select(...,...)`, `store(...,...,...)`.
- If both of the above are used, the parser will use the Theory of Equality and treats the other two as uninterpreted functions and predicates.

### Syntax Transformation

If the following literals are used in the input file, the parser will transform them:
- `~atom(x)` to `cons(x_1,x_2) = x`
- `select(a,b) = j` to `select_a(b) = j`

## Output

The output file will contain the following information:
- The input formulas
- The DNF form of the input formulas
- The list of cubes generated from the DNF form
- For each cube:
    - The term set SF
    - The procedure of the Congruence Closure Algorithm if in `verbose` mode
    - If there is a conflict
- The result SAT or UNSAT
- The total elapsed time for the satisfiability resolution (do not takes in account the DNF transformation).