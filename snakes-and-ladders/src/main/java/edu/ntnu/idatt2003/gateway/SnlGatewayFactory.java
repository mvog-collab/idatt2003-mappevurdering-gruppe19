package edu.ntnu.idatt2003.gateway;

import edu.games.engine.*;
import edu.games.engine.board.factory.JsonBoardLoader;
import edu.games.engine.board.factory.LinearBoardFactory;
import edu.games.engine.dice.factory.DiceFactory;
import edu.games.engine.dice.factory.RandomDiceFactory;
import edu.games.engine.impl.CsvPlayerStore;
import edu.games.engine.impl.overlay.JsonOverlayProvider;
import edu.games.engine.impl.overlay.OverlayProvider;
import edu.games.engine.rule.factory.RuleFactory;
import edu.games.engine.rule.factory.SnlRuleFactory;
import edu.games.engine.store.PlayerStore;

public final class SnlGatewayFactory {

    public static SnlGateway createDefault() {
        JsonBoardLoader boardLoader  = new LinearBoardFactory();
        RuleFactory     ruleFactory  = new SnlRuleFactory();
        DiceFactory     diceFactory  = new RandomDiceFactory();
        PlayerStore     playerStore  = new CsvPlayerStore();
        OverlayProvider overlayProv  = new JsonOverlayProvider("/overlays/");

        return new SnlGateway(boardLoader, ruleFactory,
                              diceFactory,  playerStore, overlayProv);
    }

    private SnlGatewayFactory() { }
}