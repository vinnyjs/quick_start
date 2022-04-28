namespace smithy4s.hello

use smithy4s.api#simpleRestJson

@simpleRestJson
service HelloWorldService {
  version: "1.0.0",
  operations: [Hello, Search]
}

@http(method: "POST", uri: "/{name}", code: 200)
operation Hello {
  input: Person,
  output: Greeting
}

@readonly
@http(method: "GET", uri: "/search", code: 200)
operation Search {
  input: SearchInput,
  output: SearchResults
}

structure SearchInput {
  @required
  @httpQuery("q")
  queryString: String
}

structure SearchResults {
  @required
  results: String
}



structure Person {
  @httpLabel
  @required
  name: String,

  @httpQuery("town")
  town: String
}

structure Greeting {
  @required
  message: String
}