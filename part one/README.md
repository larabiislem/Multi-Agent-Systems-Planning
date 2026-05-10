# JADE Auction Project (Part One)

## Project structure

```text
part one/
├─ src/
│  └─ auction/
│     ├─ AuctionMain.java
│     ├─ SellerAgent.java
│     ├─ BuyerAgent.java
│     ├─ AuctionMessages.java
│     └─ AuctionLogger.java
└─ README.md
```

## Requirements

- Java (JDK 8+)
- JADE library (`jade/lib/jade.jar`)

## Compile

From the repository root:

```bash
mkdir -p out
javac -cp "jade/lib/jade.jar" -d out "part one"/src/auction/*.java
```

## Run

```bash
java -cp "out:jade/lib/jade.jar" auction.AuctionMain
```

> On Windows, replace `out:jade/lib/jade.jar` with `out;jade/lib/jade.jar`.

## Hardcoded parameters

In `SellerAgent.java`:
- `START_PRICE = 100`
- `RESERVE_PRICE = 300`
- `TIMEOUT_ROUNDS = 5`
- Auction tick period: `2000 ms`
- Bid collection window per round: `1500 ms`

In `BuyerAgent.java`:
- Random budget per buyer: `200 + random.nextInt(400)`
- Bid increment logic: `current + 10 + random.nextInt(40)`
- Buyers are hardcoded as `buyer1`, `buyer2`, `buyer3`
