package com.cari.web.server.controller;

import com.cari.web.server.model.Aesthetic;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AestheticController {

    @GetMapping(value="/aesthetic/{urlName}")
    public Aesthetic index(@PathVariable String urlName) {
        String description = "A network of aesthetics emerging in the late 1980s, some an "
            + "evolution of prog-punk-zolo memphis-y squiggles, keith haring, woodcut revival, a "
            + "\"return-to-the-natural\" handrawing movement, reaction against the computer aided "
            + "design boom, late 80s environmentalism revival, etc. Still very postmodern in the "
            + "sense of appropriating seemingly endless prior artistic movements, mainly for "
            + "commercial/corporate purposes. Peaks in the mid 1990s, falling out of favor later "
            + "on as the pendulum swung back to the minimalism/tech/clean vibes of "
            + "<a href=\"/aesthetic/y2k\">Y2K</a> & <a href=\"/aesthetic/gen-x-soft-club\">"
            + "Gen X/YAC</a>. It's very wide-ranging and could be split into many sub-groups, but "
            + "this format seems to work better. Common motifs include: woodcuts, 'tribal/ancient "
            + "imagery and iconography', moons, suns, spirals, hands, eyes, stars, simple styled "
            + "flowing/curvy figures, 'aroma swirls', coffee cups, natural elements like trees/"
            + "waves/landscapes, earth tones, hand-drawn look, 'airbrushed dirty look', the earth/"
            + "globe, hearts, colorful gradated backgrounds, rough irregular borders & lines. "
            + "Overlaps with 'pop surrealism' from the same time period, though GVC is usually "
            + "trying to convey 'sincerity' as much it is needed to sell something; sorta faux-"
            + "naive, down to earth, warm.";

        if(urlName.equals("global-village-coffeehouse")) {
            return new Aesthetic(1L, "Global Village Coffeehouse",
                    "global-village-coffeehouse", "Gv", 1985, 1995, description);
        }
        
        return null;
    }

}
