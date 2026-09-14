# Executive Summary

A plain-language description of what the Network Management System is and what it does. It assumes no knowledge of the code. For technical depth, follow the links at the bottom.

## The problem it solves

Waste picker cooperatives collect recyclable material and sell it to buyers. Before this system, the work that decides a cooperative's income is hard to see: how much each worker collected, how much material is in stock right now, what a fair price is, and whether it is better to sell alone or together with neighbouring cooperatives.

The Network Management System is one application that records that work and turns it into numbers people can act on. It serves several cooperatives at once, keeping each one's data separate while allowing them to trade jointly when it pays off.

## Who uses it

Three kinds of users, distinguished by the role stored on their account.

Workers collect material. Their weighings are registered in the system, and they can see their own productivity, achievements, level, and position on the leaderboard.

Managers run one cooperative. They see their own cooperative's stock, revenue, and worker performance, create and complete sales, join collective sales with other cooperatives, publish notices, and pull reports.

Admins work across cooperatives. They register weighings, adjust the values used by the gamification layer, and read data for any cooperative by naming it explicitly.

Everyone logs in with a CPF number and a password. The system issues a token that carries the role and the cooperative, and every later request is checked against it. A manager cannot read another cooperative's data even by asking for it directly.

## What it does

Material intake. When a bag of material is weighed, the system compares the reading with what that bag weighed last time and records only the difference. A bag that is topped up and re-weighed therefore counts once, not twice. The difference is added to the cooperative's stock in the same operation.

Stock. Each cooperative has a running balance per material: total collected, total sold, and what is currently available. Everything else reads from that balance.

Normal sales. A manager registers a sale to a buyer with a material, a weight, a price per kilo and an expected date. The sale can be edited while it is open, then either completed, which stamps the sale date and moves the weight out of stock, or cancelled.

Collective sales. Several cooperatives sell one material together to reach a volume that commands a better price. One cooperative creates the sale and invites others. Each participant states how much it will contribute, and that amount is immediately set aside from its stock so it cannot be promised twice. Participants can adjust their contribution or leave, and the creator confirms the sale when it goes through. Revenue is split by contributed weight. If the sale is cancelled, every reservation is returned.

Reports. Both kinds of sale produce a report, readable as data or downloadable as a PDF. The collective sale report shows each participant's contribution and share.

Analytics. Managers get their cooperative's performance, per-worker productivity, revenue, stock by material, and recent sale prices for a material across cooperatives, which gives a reference point when negotiating.

Notice board. Notices can be published to one cooperative or to everyone, with a priority and an expiry date. Content is cleaned of unsafe HTML before it is stored.

Gamification. Workers earn achievements for collected weight, days worked, and achievements reached. Achievements give XP, XP gives levels, and levels feed a leaderboard. Multipliers adjust the weighting: a cooperative can make a specific material worth more XP, and each month every cooperative gets a random multiplier, which keeps the leaderboard from settling permanently. Three background jobs do this work on their own: one sets the monthly random multipliers, one evaluates achievements and recalculates levels every night, and one saves the weekly and monthly leaderboard standings.

## How it is built

One Spring Boot application on Java 25, storing everything in a PostgreSQL database of 23 tables. The same deployment serves two things: a REST API of roughly 60 endpoints, documented and browsable through Swagger, and four web pages rendered by the server (login, dashboard, normal sales, collective sales). PDF reports are produced from HTML templates.

The code is about 7,500 lines across 96 files, organised by subject: authentication, analytics, buyers, materials and stock, normal sales, collective sales, reports, notice board, multipliers, and three gamification modules for achievements, levels, and leaderboards.

The application is packaged as a Docker image. Pushing to the `main` branch builds it and deploys it to a self-hosted server automatically. Database credentials and the token signing key are supplied as environment variables at deploy time.

## Current state

All the flows described above are implemented and running. The project has around 309 commits made between February and May 2026.

Open items, including the ones worth prioritising, are tracked in [[Planning/Known Gaps and Follow-ups|Known Gaps and Follow-ups]].

## Related Notes

- [[Architecture/System Overview|System Overview]]
- [[API/Authentication and Roles|Authentication and Roles]]
- [[Domain/Normal Sales|Normal Sales]]
- [[Domain/Collective Sales|Collective Sales]]
- [[Domain/Gamification|Gamification]]
- [[Planning/Code Inventory|Code Inventory]]
- [[Operations/Build Test Deploy|Build, Test, and Deploy]]
