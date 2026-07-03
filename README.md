URL Shortener

A production-oriented URL shortener built with Java 21, Spring Boot, PostgreSQL, and Redis.

The goal of this project is not just to build a working URL shortener, but to explore how production systems solve problems such as distributed ID generation, scalability, and fault tolerance.

⸻

Architecture

                +----------------------+
                |      REST API        |
                +----------+-----------+
                           |
                           v
                  +-----------------+
                  | Spring Services |
                  +--------+--------+
                           |
          +----------------+----------------+
          |                                 |
          v                                 v
     PostgreSQL                        Redis
   (Persistent Data)             (ID Allocation / KGS)

⸻

Tech Stack

* Java 21
* Spring Boot
* PostgreSQL
* Redis
* Docker & Docker Compose
* Sqids

⸻

High-Level Flow

Client
   |
POST /shorten
   |
   v
URL Service
   |
Obtain Unique ID
   |
Encode using Sqids
   |
Store Mapping
   |
Return Short URL

⸻

Design Evolution

This project went through several iterations before arriving at the current architecture.

Initial Idea — Snowflake IDs

The original design used a Snowflake-style ID generator.

Application
     |
     v
Snowflake Generator
     |
     v
64-bit Unique ID

Advantages

* Distributed.
* No collisions.
* Time-ordered IDs.
* Very high throughput.

However, this introduced an additional distributed component solely for generating IDs.

⸻

Second Idea — Dedicated Key Generation Service (KGS)

The next design followed the architecture commonly discussed in distributed system design interviews.

Application
      |
      v
+-------------+
|     KGS     |
+-------------+
      |
      v
Generate IDs

The KGS would be responsible for generating globally unique IDs and distributing them to application servers.

Advantages

* Centralized ID generation.
* Collision-free.
* Easy coordination.

Drawback

The KGS itself becomes another service that must be deployed, monitored, scaled, and made highly available.

⸻

Final Design — Merge Snowflake and KGS

Rather than maintaining two independent components, the final design combines both ideas.

Instead of creating a dedicated KGS service, the application uses Redis as the ID allocation backend.

Application Server
        |
        v
Redis
(ID Allocation)
        |
        v
Application

Each application instance requests a range of IDs from Redis.

Example:

Redis
100000 ───────────────► 109999
                         |
                         v
                  Application Server

The application then serves IDs from memory until the range is exhausted.

When necessary, it requests another range.

⸻

Why This Design?

This combines the strengths of both approaches.

No Dedicated KGS

There is no extra Spring Boot service whose only responsibility is generating IDs.

Horizontal Scalability

Every application instance can independently serve requests using its locally cached range.

Server A
100000 - 109999
Server B
110000 - 119999
Server C
120000 - 129999

No coordination is required during normal request processing.

⸻

Very Few Redis Calls

Redis is only contacted when requesting a new range.

Millions of requests may be served while Redis is idle.

⸻

Collision Free

Since Redis atomically allocates non-overlapping ranges, every generated ID is unique.

⸻

Stateless Application

Application servers keep only a temporary in-memory allocation range.

Persistent data remains in PostgreSQL.

Application servers can therefore be scaled horizontally.

⸻

Future Improvements

The current design is intentionally simple while providing a clear upgrade path.

Redis Cluster

The next step is deploying Redis in a clustered/high-availability configuration.

Benefits include:

* High availability
* Replication
* Automatic failover
* Horizontal scalability

This removes Redis as a single point of failure and makes the ID allocation layer production-ready.

⸻

Dedicated KGS (Optional)

If required at very large scale, the allocation logic can be extracted into its own Key Generation Service.

The application code would remain largely unchanged because the allocation interface stays the same.

⸻

Analytics

Future work includes:

* Click tracking
* Geographic analytics
* Browser and device statistics
* Referrer analytics

⸻

Authentication

Support for authenticated users using OAuth providers such as Google.

⸻

Custom Aliases

Allow users to choose custom short URLs while enforcing uniqueness.

⸻

Caching

Redis can also be used to cache frequently accessed URL mappings, reducing database load.

⸻

Project Goals

* Learn distributed system design through implementation.
* Build a production-oriented backend using Spring Boot.
* Explore scalable ID generation strategies.
* Design for horizontal scaling.
* Keep components modular so individual services can evolve independently.

⸻

Current Status

* Dockerized infrastructure
* PostgreSQL schema
* Redis integration
* Sqids-based short code generation
* Foundation for scalable ID allocation

The project is being developed incrementally, with an emphasis on clean architecture and production-ready design decisions rather than simply implementing CRUD endpoints.