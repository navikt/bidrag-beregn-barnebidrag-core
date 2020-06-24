package no.nav.bidrag.beregn.underholdskostnad.periode;

import java.time.LocalDate;
import java.time.Period;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;

public class UnderholdskostnadPeriodeImpl implements UnderholdskostnadPeriode{



//  var soknadsbarnAlder = beregnSoknadbarnAlder(beregnUnderholdskostnadGrunnlag);







  @Override
  public Integer beregnSoknadbarnAlder(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag) {

    LocalDate tempSoknadbarnFodselsdato = beregnUnderholdskostnadGrunnlag.getSoknadBarnFodselsdato()
        .withDayOfMonth(01)
        .withMonth(07);

    System.out.println("tempSoknadbarnFodselsdato: " + tempSoknadbarnFodselsdato);

    Integer beregnetAlder = Period.between(tempSoknadbarnFodselsdato, LocalDate.now()).getYears();

    System.out.println("Beregnet alder: " + beregnetAlder);

    return beregnetAlder;

  }





}
