[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.kacurez/clj-parallels.svg)](https://clojars.org/org.clojars.kacurez/clj-parallels)
[![Build Status](https://travis-ci.org/kacurez/clj-parallels.svg?branch=master)](https://travis-ci.org/kacurez/clj-parallels)

# clj-parallels

A Clojure library designed to execute a function on a data sequence in parallel. Primary intended as my study of
- async programming via `clojure/core.async`
- generative testing via `clojure.spec`
- CI developement of clojure library via `travis`

## Installation
with Leiningen:
```
[org.clojars.kacurez/clj-parallels "0.3.0"]
```

## Documentation

### pexecute
`(pexecute execute-fn input-seq workers-cnt)` `(pexecute execute-fn input-seq)`

Executes `execute-fn` on each item from `input-seq` in parallel. The maximum number of running parallel executions at one time is specified by `workers-cnt`. Returns sequence of applied results in no particular order. If one of the executions throws an exception then `pexecute` waits for all currently running executions to finish and throws the exception.



## Usage

```clojure
user> (require '[kacurez.clj-parallels :refer [pexecute]])
nil
user> (pexecute inc [1 2 3] 3)
(4 3 2)
user> (pexecute inc [1 2 3 4 5 6] 3)
(7 6 5 4 3 2)
user> (pexecute inc [1 2 3 4 5 6] 2)
(7 6 5 4 3 2)
user> (pexecute inc [1 2 3 4 5 6 7 8] 2)
(9 8 7 6 5 4 3 2)
user> (pexecute inc [1 2 3 4 5 6 7 8] 10)
(9 8 7 6 5 4 3 2)
user>
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
