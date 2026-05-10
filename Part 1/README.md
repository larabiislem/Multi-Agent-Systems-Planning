# Part 1 - Multi-Agent Negotiation (English Auction with JADE)

## Overview
This part implements a **standard English auction** using JADE:
- **1 seller** agent
- **multiple buyers** (minimum 2, implemented with 3 buyers)
- ascending bids round by round
- sale only if reserve price is met

The auction stops when:
1. all buyers stop bidding in a round, or
2. maximum number of rounds is reached.

## Project Structure
```text
Part 1/
├─ README.md
└─ src/
   └─ auction/
      ├─ AuctionMain.java
      ├─ SellerAgent.java
      ├─ BuyerAgent.java
      ├─ AuctionMessages.java
      └─ AuctionLogger.java
```

## Requirements
- Java 8 or later
- JADE library (`jade.jar`)

## Hardcoded Parameters (Spec)
In `SellerAgent.java`:
- **Start/Open price:** `100`
- **Reserve price (seller only):** `300`
- **Maximum rounds:** `5`

Additional runtime settings:
- round period: `2500 ms`
- bid collection window per round: `1500 ms`

## Compile
From repository root:

```bash
javac -cp "jade.jar" -d "Part 1/out" "Part 1/src/auction"/*.java
```

## Run
### Linux / macOS
```bash
java -cp "jade.jar:Part 1/out" auction.AuctionMain
```

### Windows
```bat
java -cp "jade.jar;Part 1/out" auction.AuctionMain
```

## Usage Notes
- JADE GUI is enabled in `AuctionMain` (`Profile.GUI=true`).
- In JADE RMA, optionally open **Sniffer** and monitor:
  - `seller`
  - `buyer1`
  - `buyer2`
  - `buyer3`
- Console output provides a clear execution trace for each round.

## Parameters and Behavior
- Seller sends a CFP (call for proposal) each round with current asking price.
- Buyers submit bids only if current ask is below their budget.
- Seller broadcasts highest bid after each round.
- At end:
  - if highest final bid `>= reserve price`, winner is notified and item is sold.
  - otherwise, item remains unsold.

## Example Output (Console)
```text
[14:10:11] [seller] Starting English auction for 'Product' (start=100, reserve=300, maxRounds=5)
[14:10:13] [seller] Round 1 - asking price: 100
[14:10:13] [buyer1] Bids 131 (ask 100)
[14:10:13] [buyer2] Bids 145 (ask 100)
[14:10:14] [seller] Highest bid now 145 by buyer2
...
[14:10:22] [seller] Maximum rounds reached.
[14:10:22] [seller] Item sold to buyer2 at 326
[14:10:22] [buyer2] I won the auction! Won with final price=326
```
