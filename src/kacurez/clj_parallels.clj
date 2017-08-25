(ns kacurez.clj-parallels
  (:require [clojure.spec.test.alpha :as stest]
            [clojure.spec.alpha :as s]
            [
             clojure.core.async
             :as async
             :refer [>!! <!! chan buffer close! thread poll! dropping-buffer
                     alts!! timeout]]))

(defn- run-process [id input-chan error-chan results-chan process-fn]
  (thread
    (try
      (loop [value (<!! input-chan)]
        (if value
          (do
            ; make sure we put non nil value into result channel
            (>!! results-chan {:result (process-fn (:input value))})
            (recur (<!! input-chan)))
            ; process has finished(no input) and returns true
          true))
      (catch Throwable e
        (>!! error-chan {:id id :data e})))))

(defn- finalize [process-channels results-chan input-chan error-chan]
  (close! input-chan)
  ; wait for threads to finish - collect return values from their channels
  (mapv #(<!! %) process-channels)
  (close! results-chan)
  (close! error-chan))


(s/fdef pexecute :args (s/cat :execute-fn fn? :input-coll (s/nilable (s/coll-of any?)) :workers-cnt (s/? (s/and int? #(> % 0)))))
(defn pexecute
  "takes items from @input-coll and apply @execute-fn to every item from the collection in parallel.
  The number of maximum parallel executions at time is defined by @workers-cnt. Returns collection of applied results.
  The order of results is not kept. Throws error and won't continue if one of the executions throws error."
  ([execute-fn input-coll]
   (pexecute execute-fn input-coll 5))
  ([execute-fn input-coll workers-cnt]
   (let [input-chan (chan)
         error-chan (chan (dropping-buffer 1))
         results-chan (chan (buffer (count input-coll)))
         create-process (fn [pid] (run-process pid input-chan error-chan results-chan execute-fn))
         processors-chans (doall (map create-process (range 0 workers-cnt)))]
     (loop [input input-coll]
       (if (not-empty input)
         (let [item (first input)
               [chan-result chan-taken] (alts!! [error-chan [input-chan {:input item}]])]
           (if (= chan-taken input-chan)
             (recur (rest input))
             ; resend error to error channel so it can be collected
             ; after finalization
             (>!! error-chan chan-result)))))
     (finalize processors-chans results-chan input-chan error-chan)
     (if-let [error (poll! error-chan)]
       (throw (ex-info "Error encountered" error)))
     ; collect all results from result channels and return them
     (map :result (<!! (async/into '() results-chan))))))


(defn- safe-println [& more]
  (comp (fn [_] (. *out* (flush)))
        (.write *out* (str (clojure.string/join " " more) "\n")))
  (flush))

(stest/instrument `pexecute)
