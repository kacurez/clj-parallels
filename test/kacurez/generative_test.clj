(ns kacurez.generative-test
  (:require
   [clojure.test.check :as tc]
   [clojure.spec.alpha :as s]
   [clojure.spec.test.alpha :as stest]
   [clojure.test :refer :all]
   [kacurez.clj-parallels :refer :all]))


(defn inc-or-throw [input]
  (Thread/sleep (* 10 input))
  (if (zero? input)
    (throw (ex-info "Zero encountered"))
    (inc input)))

(defn check-result [{:keys [ret args] :as val}]
  (let [input-col (:input-coll args)]
    (if (contains? (set input-col) 0)
      (nil? ret)
      (= (+ (count input-col) (reduce + input-col))
         (reduce + ret)))))

(s/def ::input-col (s/coll-of (s/and int? #(>= % 0) #(< % 500))
                              :max-count 10 :distinct true))
(s/def ::workers-cnt (s/and int? #(> % 0) #(< % 100)))
(s/fdef pexecute-gen :args (s/cat :input-coll ::input-col :workers-cnt ::workers-cnt)
        :ret (s/nilable (s/coll-of int?))
        :fn check-result)

(defn pexecute-gen [input-coll workers-cnt]
  (print "test input-coll:" input-coll "workers-cnt:" workers-cnt "ETA sequential:" (* 10 (reduce + input-coll)) "ms ")
  (let [result (time (try
                       (pexecute inc-or-throw input-coll workers-cnt)
                       (catch Throwable e
                         (if (= (.getMessage e) "Error encountered")
                           nil
                           (throw e)))))]
    (if (nil? result) (print " throwed\n"))
    result))

(defn test-and-check
  ([spec-test] (test-and-check spec-test 100))
  ([spec-test num-tests]
   (let [result (stest/summarize-results
                 (stest/check spec-test {:clojure.spec.test.check/opts {:num-tests num-tests}}))]
     (= (:total result) (:check-passed result)))))

(deftest generative-test
  (testing "generative tests"
    (is (test-and-check `pexecute-gen 3))))
