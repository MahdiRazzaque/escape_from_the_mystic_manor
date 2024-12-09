# Mystic Manor: A Text-Based Adventure Game

## Overview

Welcome to the Mystic Manor, a captivating text-based adventure game where you find yourself trapped within the eerie walls of an ancient manor. Your mission is to explore enchanted rooms, interact with intriguing characters, collect mystical items, and solve puzzles to ultimately escape the manor's grasp. This game offers a unique and immersive experience, blending elements of fantasy and adventure to create an engaging narrative.

This project was developed as part of the second assignment for the PPA module at KCL and is written in Java.

## Main Features

-   **Exploration:** Traverse through a variety of mystical rooms, each with its own unique description, items, and secrets. Discover hidden chambers and unravel the mysteries of the Mystic Manor.
-   **Character Interaction:** Engage with a diverse cast of characters, including a butler, maid, security guard, a talking cat, and even the ghost of the manor's former owner. Each character possesses unique dialogue and may offer clues, challenges, or items crucial to your escape.
-   **Inventory Management:** Collect and manage an inventory of mystical items. Utilize these items to solve puzzles, overcome obstacles, and interact with the environment. Be mindful of your inventory's weight limit, as it restricts the number of items you can carry.
-   **Dynamic Character Movement:** Experience dynamic character movements based on difficulty settings. Once interacted with, characters can randomly move around the map, adding a layer of unpredictability and challenge to the game.
-   **Puzzle Solving:** Solve the riddle of the cat to obtain a key weapon necessary to progress in your adventure. Decipher cryptic messages from the ancient book and utilize other items to unlock hidden paths and overcome challenges.
-   **Locked Doors and Keys:** Encounter locked doors that require specific keys to unlock. Search for these keys hidden throughout the manor, often guarded by characters or concealed within rooms.
-   **Map System:** If enabled, utilize a map to navigate the manor's layout. This feature can assist in planning your route and tracking your progress through the various rooms.
-   **Combat:** Battle against the ghost of the former owner using a specific item that you can acquire from the cat.

## How to Play

1. **Start the Game:** Run the `Main` class to start the game. You will be greeted with a welcome message and prompted to configure game settings.
2. **Game Settings:**
    -   **Map:** Choose whether to enable the map feature.
    -   **Random Character Movement:** Decide if characters should move randomly around the map after being interacted with.
    -   **Difficulty:** If random character movement is enabled, select the difficulty level (easy, medium, or hard), which affects the frequency of character movements.
3. **Navigation:** Use the `go` command followed by a direction (north, east, south, west) to move between rooms. For example, `go north`. Use the `back` command to return to the previous room.
4. **Interaction:**
    -   **Characters:** Use the `interact` command followed by the character's name (e.g., `interact butler`) to engage in conversation.
    -   **Items:**
        -   `inventory display`: View your current inventory.
        -   `inventory pickup [item] [quantity]`: Pick up an item from the current room.
        -   `inventory drop [item] [quantity]`: Drop an item from your inventory.
        -   `use [item]`: Use an item from your inventory. For some items, you may need to specify a target, e.g., `use jewelled_dagger ghost_of_the_former_owner`.
5. **Room Information:** Use the `room info` command to display details about the current room, including exits, items, and characters present.
6. **Map:** If enabled, use the `map` command to view a visual representation of the manor's layout.
7. **Quitting:** Use the `quit` command to exit the game.
8. **Help:** Type `help` to see a list of available commands and their descriptions.
9. **Solving the Riddle:** To solve the cat's riddle, use the command `answer [your answer]` when in the same room as the cat and after interacting with it. You will need at least five coins to proceed.
10. **Winning the Game** Consume the `holy_bread` to win the game


## Development

This game is written in Java and structured into several classes, each responsible for a specific aspect of the game:

-   **`Character`:** Represents a character in the game, with attributes like health, passivity, inventory, and location.
-   **`Command`:** Stores information about a command issued by the user.
-   **`CommandWords`:** Holds an enumeration of all valid command words known to the game.
-   **`Dialog`:** Manages the dialog interactions for various characters.
-   **`Game`:** The main class that initializes and runs the game.
-   **`Inventory`:** Manages the player's inventory.
-   **`Item`:** Represents an item in the game.
-   **`lockedDoor`:** Represents a door that requires a key to unlock.
-   **`Main`:** The entry point of the game application.
-   **`nonPlayerInventory`:** Manages the inventory for non-player entities like rooms and characters.
-   **`Parser`:** Reads user input and interprets it as commands.
-   **`Room`:** Represents a location in the game.
-   **`Utils`:** Provides utility functions used throughout the game.
- 
Enjoy your adventure in the Mystic Manor!