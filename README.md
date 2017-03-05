# kosmos

kosmos -- greek for 'ordered system'

This library is a recreation of Semperos' liftoff library. It is built on top of Stuart
Sierra's component library. The purpose is to bring an ordered system
into existence based on a simple declarative configuration map and to be able to
reference the assembled components and dependencies at runtime.

[![Clojars Project](https://img.shields.io/clojars/v/kosmos.svg)](https://clojars.org/kosmos)

## Usage

Kosmos uses a simple map to describe configure components. More information about how to configure components can be found in the respective component projects' README/docs. Here is a quick example of how to configure a db component, a web server, and a repl component.

```clojure

{
 :pool
 {
  :auto-commit         true
  :maximum-pool-size   10
  :pool-name           "application-db-pool"
  }

 :db
 {
  :kosmos/init     :kosmos.db/DbComponent
  :kosmos/requires [:pool]
  :classname       "com.mysql.jdbc.Driver"
  :subprotocol     "mysql"
  :host            "db.server.com"
  :port            3306
  :database        "important_database"
  :user            "username"
  :password        "password"
  }

 :server
 {
  :kosmos/init  :kosmos.web/RingJettyComponent
  :kosmos/requires [:db]
  :ring-app     :application.server/app
  :configurator :application.server/configurator
  :port         8081
  :join?        false
 }

 :nrepl
 {
  :kosmos/init :kosmos.nrepl/NreplComponent
  :port 9001
  }
}
```

For more information on these specific components see:

[![Clojars Project](https://img.shields.io/clojars/v/kosmos/kosmos-hikari.svg)](https://clojars.org/kosmos/kosmos-hikari)  
[![Clojars Project](https://img.shields.io/clojars/v/kosmos/kosmos-web.svg)](https://clojars.org/kosmos/kosmos-web)  
[![Clojars Project](https://img.shields.io/clojars/v/kosmos/kosmos-nrepl.svg)](https://clojars.org/kosmos/kosmos-nrepl)  

## License

Kosmos is distributed under the [Eclipse Public License](http://opensource.org/licenses/eclipse-1.0.php), the same as Clojure.