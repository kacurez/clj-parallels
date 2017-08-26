(ns kacurez.clj-parallels-test
  (:require [clojure.test :refer :all]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]
            [kacurez.clj-parallels :refer :all]))

(deftest pexecute-test
  (testing "execute and compare result"
    (let [input [1 2 3 4 5]
          result-seq (atom '())
          result (pexecute #(swap! result-seq conj %) input 3)]
      (is (= (set input) (set @result-seq)))
      (is (= (count result) (count input)))))

  (testing "nil results, should pass"
    (let [result (pexecute println [1 2 3] 10)]
      (is (every? nil? result))))

  (testing "nil input, should return empty list"
    (let [result (pexecute identity nil 10)]
      (is (= result '()))))

  (testing "input as list of nils, should pass"
    (let [result (pexecute identity [nil nil nil] 10)]
      (is (every? nil? result))))

  (testing "empty input sequence, should return empty list"
    (let [result (pexecute println [])]
      (is (= result '()))))

  (testing "all input fails, should throw exception"
    (let [throw-execute-fn #(pexecute (fn [a] (/ 1 a)) (take (+ 1 (rand-int 10)) (repeat 0)) 10)]
      (dotimes [n 8]
        (is (thrown-with-msg? Throwable #"Error encountered" (throw-execute-fn))))))

  (testing "some input fails, should throw exception"
    (let [throw-execute-fn #(pexecute (fn [a] (/ 1 a)) [1 2 0 3 4 5] 10)]
      (dotimes [n 3]
        (is (thrown-with-msg? Throwable #"Error encountered" (throw-execute-fn))))))

  (testing "zero workers count, should throw exception"
    (let [throw-execute-fn #(pexecute println [1 2] 0)]
      (is (thrown-with-msg? Throwable #"did not conform to spec" (throw-execute-fn))))))
