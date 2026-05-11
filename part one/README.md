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
│     ├─ AuctionLogger.java
│     ├─ AuctionModel.java
│     ├─ AuctionPanel.java
│     └─ AuctionFrame.java
└─ README.md
```

## Requirements

- Java (JDK 8+)
- JADE library (`jade/lib/jade.jar`)

## Compile

From the repository root:

```bash
mkdir -p "part one"/out
javac -cp "jade/lib/jade.jar" -d "part one"/out "part one"/src/auction/*.java
```

## Run

```bash
java -cp "part one/out:jade/lib/jade.jar" auction.AuctionMain
```

> On Windows, replace `part one/out:jade/lib/jade.jar` with `part one/out;jade/lib/jade.jar`.

## Notes

- A Swing auction dashboard (matching the style of Part 4) opens alongside the JADE RMA GUI.

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
