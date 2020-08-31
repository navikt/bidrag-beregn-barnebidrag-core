package no.nav.bidrag.beregn.bidragsevne.periode;

import java.util.List;
import no.nav.bidrag.beregn.bidragsevne.beregning.Bidragsevneberegning;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlagAlt;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneResultat;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriodeImpl;
import no.nav.bidrag.beregn.felles.bo.Avvik;

public interface BidragsevnePeriode {
    BeregnBidragsevneResultat beregnPerioder(
        BeregnBidragsevneGrunnlagAlt beregnBidragsevneGrunnlagAlt);
    List<Avvik> validerInput(BeregnBidragsevneGrunnlagAlt beregnBidragsevneGrunnlagAlt);
    static BidragsevnePeriode getInstance() {
        return new BidragsevnePeriodeImpl(Bidragsevneberegning.getInstance());
    }
}
