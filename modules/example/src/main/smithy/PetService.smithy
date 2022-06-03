namespace smithy4s.pet

use smithy4s.api#simpleRestJson

structure Pet {
    @httpLabel
    @required
    id: String,
    @httpPayload
    name: String,
}

structure Result {
    @required
    message: String
}

structure GetIdParam {
    @required
    @httpLabel
    id: String
}

structure allPets {
    pets: PetList
}

@input
structure CreatePet {
	id: String,
    name: String
}


list PetList {
    member: Pet
}

@http(method: "GET", uri: "/", code: 200)
operation GetAll {
    output: allPets
}


@http(method: "GET", uri: "/{id}", code: 200)
operation GetPet {
    input: GetIdParam,
    output: Result
}

@http(method: "POST", uri: "/", code: 200)
operation PostPet {
    input: CreatePet,
    output: Result
}

@http(method: "PUT", uri: "/{id}", code: 200)
operation PutPet {
    input: Pet,
    output: Result
}


@idempotent
operation DeletePet {
    input: GetIdParam
}


@simpleRestJson
service PetService {
    version: "1.0.0",
    operations: [GetPet, PostPet, GetAll, PutPet]
}
