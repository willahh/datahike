(defproject datahike01 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [io.replikativ/datahike "0.1.2"]
                 [juxt/dirwatch "0.2.3"]]
  :repl-options {:init-ns datahike01.core})

